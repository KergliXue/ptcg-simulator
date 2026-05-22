package com.ptcg.server.websocket;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptcg.server.engine.GameArbiter;
import com.ptcg.server.engine.GameLogic;
import com.ptcg.server.entity.User;
import com.ptcg.server.mapper.UserMapper;
import com.ptcg.server.model.action.*;
import com.ptcg.server.model.prompt.GamePrompt;
import com.ptcg.server.model.state.*;
import com.ptcg.server.serializer.StateSerializer;
import com.ptcg.server.service.TokenService;
import com.ptcg.server.store.GameStoreImpl;
import com.ptcg.server.store.GameSession;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class GameSocketHandler implements GameLogic.GameStoreAdapter {

    private final SocketIOServer server;
    private final GameStoreImpl gameStore;
    private final ObjectMapper objectMapper;
    private final StateSerializer stateSerializer;
    private final TokenService tokenService;
    private final UserMapper userMapper;

    private final AtomicInteger clientIdSequence = new AtomicInteger(0);
    private final AtomicInteger gameIdSequence = new AtomicInteger(0);

    private final Map<UUID, String> clientRooms = new ConcurrentHashMap<>();
    private final Map<String, Queue<SocketIOClient>> matchQueue = new ConcurrentHashMap<>();
    private final Map<String, GameLogic> gameLogics = new ConcurrentHashMap<>();
    private final Set<String> resolvingArbiter = new HashSet<>();

    public GameSocketHandler(SocketIOServer server, GameStoreImpl gameStore,
                             ObjectMapper objectMapper, TokenService tokenService,
                             UserMapper userMapper) {
        this.server = server;
        this.gameStore = gameStore;
        this.objectMapper = objectMapper;
        this.stateSerializer = new StateSerializer();
        this.tokenService = tokenService;
        this.userMapper = userMapper;
    }

    @OnConnect
    public void onConnect(SocketIOClient client) {
        System.out.println("Client connected: " + client.getSessionId());
        try {
            String token = client.getHandshakeData().getSingleUrlParam("token");
            long userId = tokenService.validateToken(token);
            if (userId > 0) {
                User user = userMapper.selectById(userId);
                if (user != null) {
                    int clientId = clientIdSequence.incrementAndGet();
                    client.set("clientId", clientId);
                    client.set("userId", userId);
                    System.out.println("User authenticated: " + user.getName() + " (clientId: " + clientId + ", userId: " + userId + ")");

                    user.setLastSeen(System.currentTimeMillis());
                    userMapper.updateById(user);

                    Map<String, Object> clientUserData = Map.of(
                            "clientId", clientId,
                            "user", buildUserInfo(user, true)
                    );
                    server.getBroadcastOperations().sendEvent("core:join", clientUserData);
                } else {
                    client.disconnect();
                }
            } else {
                client.disconnect();
            }
        } catch (Exception e) {
            System.err.println("Error authenticating client connection: " + e.getMessage());
            client.disconnect();
        }
    }

    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        System.out.println("Client disconnected: " + client.getSessionId());

        Integer clientId = client.get("clientId");
        if (clientId != null) {
            server.getBroadcastOperations().sendEvent("core:leave", clientId);
        }

        // Cleanup from matchmaking queues
        for (Queue<SocketIOClient> q : matchQueue.values()) {
            q.remove(client);
        }

        String room = clientRooms.remove(client.getSessionId());
        if (room != null) {
            client.leaveRoom(room);
            GameLogic logic = gameLogics.get(room);
            if (logic != null) {
                int playerId = findPlayerIdForClient(room, client);
                if (playerId >= 0) {
                    logic.dispatch(new AbortGameAction(playerId,
                            AbortGameAction.AbortReason.DISCONNECTED));
                }
            }
        }
    }

    // === core events (ack-expected) ===

    @OnEvent("core:getInfo")
    public void onCoreGetInfo(SocketIOClient client, AckRequest ack) {
        try {
            Integer callingClientId = client.get("clientId");
            if (callingClientId == null) {
                callingClientId = 0;
            }

            List<Map<String, Object>> clientsList = new ArrayList<>();
            Set<Long> userIds = new HashSet<>();
            for (SocketIOClient c : server.getAllClients()) {
                Integer cId = c.get("clientId");
                Long uId = c.get("userId");
                if (cId != null && uId != null) {
                    clientsList.add(Map.of("clientId", cId, "userId", uId));
                    userIds.add(uId);
                }
            }

            List<Map<String, Object>> usersList = new ArrayList<>();
            if (!userIds.isEmpty()) {
                List<User> users = userMapper.selectBatchIds(userIds);
                for (User u : users) {
                    boolean connected = false;
                    for (SocketIOClient c : server.getAllClients()) {
                        Long uId = c.get("userId");
                        if (uId != null && uId.equals(u.getId())) {
                            connected = true;
                            break;
                        }
                    }
                    usersList.add(buildUserInfo(u, connected));
                }
            }

            List<Map<String, Object>> gamesList = new ArrayList<>();
            for (GameSession session : gameStore.getGames().values()) {
                gamesList.add(buildGameInfo(session));
            }

            Map<String, Object> coreInfo = new LinkedHashMap<>();
            coreInfo.put("clientId", callingClientId);
            coreInfo.put("clients", clientsList);
            coreInfo.put("users", usersList);
            coreInfo.put("games", gamesList);

            ack.sendAckData(Map.of("message", "ok", "data", coreInfo));
        } catch (Exception e) {
            System.err.println("Error on core:getInfo: " + e.getMessage());
            ack.sendAckData(Map.of("message", "error", "data", e.getMessage()));
        }
    }

    @OnEvent("core:createGame")
    public void onCoreCreateGame(SocketIOClient client, AckRequest ack, Object data) {
        try {
            System.out.println("core:createGame received: " + data);

            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) data;
            @SuppressWarnings("unchecked")
            List<String> deck = (List<String>) map.get("deck");
            @SuppressWarnings("unchecked")
            Map<String, Object> gameSettings = (Map<String, Object>) map.get("gameSettings");
            Integer invitedClientId = (Integer) map.get("clientId");

            Integer creatorClientId = client.get("clientId");
            Long creatorUserId = client.get("userId");
            if (creatorClientId == null || creatorUserId == null) {
                ack.sendAckData(Map.of("message", "error", "data", "ERROR_NOT_AUTHENTICATED"));
                return;
            }

            User creatorUser = userMapper.selectById(creatorUserId);
            if (creatorUser == null) {
                ack.sendAckData(Map.of("message", "error", "data", "ERROR_USER_NOT_FOUND"));
                return;
            }

            int gameId = gameIdSequence.incrementAndGet();
            String roomId = String.valueOf(gameId);

            SocketIOClient invitedClient = null;
            if (invitedClientId != null) {
                for (SocketIOClient c : server.getAllClients()) {
                    Integer cId = c.get("clientId");
                    if (cId != null && cId.equals(invitedClientId)) {
                        invitedClient = c;
                        break;
                    }
                }
            }

            GameSession session = gameStore.createGame(roomId, client, invitedClient);
            client.joinRoom(roomId);
            clientRooms.put(client.getSessionId(), roomId);

            if (invitedClient != null) {
                invitedClient.joinRoom(roomId);
                clientRooms.put(invitedClient.getSessionId(), roomId);
            }

            GameLogic logic = new GameLogic(this);
            State state = new State();

            if (gameSettings != null && gameSettings.get("formatName") != null) {
                state.getRules().setFormatName((String) gameSettings.get("formatName"));
            }

            logic.setState(state);
            session.setState(state);
            gameLogics.put(roomId, logic);

            // Add Creator as Player 1
            logic.dispatch(new AddPlayerAction(creatorClientId, creatorUserId, creatorUser.getName(), deck));

            System.out.println("Game " + roomId + " created by " + creatorUser.getName() + " (clientId: " + creatorClientId + ")");

            // ack to creator with game state
            ack.sendAckData(Map.of("message", "ok", "data", buildGameState(roomId, state, session)));

            // Notify other clients about new game
            server.getBroadcastOperations().sendEvent("core:createGame", buildGameInfo(session));
        } catch (Exception e) {
            System.err.println("Error creating game: " + e.getMessage());
            ack.sendAckData(Map.of("message", "error", "data", e.getMessage()));
        }
    }

    // === game events (ack-expected) ===

    @OnEvent("game:join")
    public void onGameJoin(SocketIOClient client, AckRequest ack, Object data) {
        try {
            System.out.println("game:join received: " + data + " (type=" + (data != null ? data.getClass().getSimpleName() : "null") + ")");

            int gameId = extractGameId(data);
            if (gameId <= 0) {
                ack.sendAckData(Map.of("message", "error", "data", "ERROR_GAME_INVALID_ID"));
                return;
            }

            String roomId = String.valueOf(gameId);
            GameSession session = gameStore.getGame(roomId);
            if (session == null) {
                ack.sendAckData(Map.of("message", "error", "data", "ERROR_GAME_INVALID_ID"));
                return;
            }

            Integer joiningClientId = client.get("clientId");
            Long joiningUserId = client.get("userId");
            if (joiningClientId == null || joiningUserId == null) {
                ack.sendAckData(Map.of("message", "error", "data", "ERROR_NOT_AUTHENTICATED"));
                return;
            }

            User joiningUser = userMapper.selectById(joiningUserId);
            if (joiningUser == null) {
                ack.sendAckData(Map.of("message", "error", "data", "ERROR_USER_NOT_FOUND"));
                return;
            }

            client.joinRoom(roomId);
            clientRooms.put(client.getSessionId(), roomId);

            // If player 2 is not set, and the client is not player 1, make them player 2!
            if (session.getPlayer2Client() == null && !session.getPlayer1Client().getSessionId().equals(client.getSessionId())) {
                session.setPlayer2Client(client);
                System.out.println("Player 2 (" + joiningUser.getName() + ", clientId: " + joiningClientId + ") joined Game " + roomId);
            } else {
                System.out.println("Observer (" + joiningUser.getName() + ", clientId: " + joiningClientId + ") joined Game " + roomId);
            }

            // Broadcast join event to the room
            server.getRoomOperations(roomId).sendEvent("game[" + gameId + "]:join", joiningClientId);

            // Return GameState ACK to joining client
            ack.sendAckData(Map.of("message", "ok", "data", buildGameState(roomId, session.getState(), session)));
        } catch (Exception e) {
            System.err.println("Error joining game: " + e.getMessage());
            ack.sendAckData(Map.of("message", "error", "data", e.getMessage()));
        }
    }

    @OnEvent("game:leave")
    public void onGameLeave(SocketIOClient client, AckRequest ack, Object data) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) data;
            int gameId = ((Number) map.get("gameId")).intValue();
            String roomId = String.valueOf(gameId);

            clientRooms.remove(client.getSessionId());
            client.leaveRoom(roomId);

            Integer clientId = client.get("clientId");
            if (clientId != null) {
                server.getRoomOperations(roomId).sendEvent("game[" + gameId + "]:leave", clientId);
            }

            ack.sendAckData(Map.of("message", "ok"));
        } catch (Exception e) {
            System.err.println("Error leaving game: " + e.getMessage());
            ack.sendAckData(Map.of("message", "error", "data", e.getMessage()));
        }
    }

    // === game actions (ack-expected) ===

    @OnEvent("game:action:playCard")
    public void onPlayCard(SocketIOClient client, AckRequest ack, Object data) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) data;
            int gameId = ((Number) map.get("gameId")).intValue();
            String roomId = String.valueOf(gameId);

            GameLogic logic = gameLogics.get(roomId);
            if (logic == null) {
                ack.sendAckData(Map.of("message", "error", "data", "ERROR_GAME_INVALID_ID"));
                return;
            }

            int playerId = findPlayerIdForClient(roomId, client);
            int handIndex = ((Number) map.get("handIndex")).intValue();

            @SuppressWarnings("unchecked")
            Map<String, Object> targetMap = (Map<String, Object>) map.get("target");
            CardTarget target = objectMapper.convertValue(targetMap, CardTarget.class);

            PlayCardAction action = new PlayCardAction(playerId, handIndex, target);
            logic.dispatch(action);
            ack.sendAckData(Map.of("message", "ok"));
        } catch (Exception e) {
            System.err.println("Error processing playCard: " + e.getMessage());
            ack.sendAckData(Map.of("message", "error", "data", e.getMessage()));
        }
    }

    @OnEvent("game:action:attack")
    public void onAttack(SocketIOClient client, AckRequest ack, Object data) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) data;
            int gameId = ((Number) map.get("gameId")).intValue();
            String roomId = String.valueOf(gameId);

            GameLogic logic = gameLogics.get(roomId);
            if (logic == null) {
                ack.sendAckData(Map.of("message", "error", "data", "ERROR_GAME_INVALID_ID"));
                return;
            }

            int playerId = findPlayerIdForClient(roomId, client);
            String attackName = (String) map.get("attack");

            AttackAction action = new AttackAction(playerId, attackName);
            logic.dispatch(action);
            ack.sendAckData(Map.of("message", "ok"));
        } catch (Exception e) {
            System.err.println("Error processing attack: " + e.getMessage());
            ack.sendAckData(Map.of("message", "error", "data", e.getMessage()));
        }
    }

    @OnEvent("game:action:retreat")
    public void onRetreat(SocketIOClient client, AckRequest ack, Object data) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) data;
            int gameId = ((Number) map.get("gameId")).intValue();
            String roomId = String.valueOf(gameId);

            GameLogic logic = gameLogics.get(roomId);
            if (logic == null) {
                ack.sendAckData(Map.of("message", "error", "data", "ERROR_GAME_INVALID_ID"));
                return;
            }

            int playerId = findPlayerIdForClient(roomId, client);
            int benchIndex = ((Number) map.get("to")).intValue();

            RetreatAction action = new RetreatAction(playerId, benchIndex);
            logic.dispatch(action);
            ack.sendAckData(Map.of("message", "ok"));
        } catch (Exception e) {
            System.err.println("Error processing retreat: " + e.getMessage());
            ack.sendAckData(Map.of("message", "error", "data", e.getMessage()));
        }
    }

    @OnEvent("game:action:passTurn")
    public void onPassTurn(SocketIOClient client, AckRequest ack, Object data) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) data;
            int gameId = ((Number) map.get("gameId")).intValue();
            String roomId = String.valueOf(gameId);

            GameLogic logic = gameLogics.get(roomId);
            if (logic == null) {
                ack.sendAckData(Map.of("message", "error", "data", "ERROR_GAME_INVALID_ID"));
                return;
            }

            int playerId = findPlayerIdForClient(roomId, client);
            PassTurnAction action = new PassTurnAction(playerId);
            logic.dispatch(action);
            ack.sendAckData(Map.of("message", "ok"));
        } catch (Exception e) {
            System.err.println("Error processing passTurn: " + e.getMessage());
            ack.sendAckData(Map.of("message", "error", "data", e.getMessage()));
        }
    }

    @OnEvent("game:action:ability")
    public void onUseAbility(SocketIOClient client, AckRequest ack, Object data) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) data;
            int gameId = ((Number) map.get("gameId")).intValue();
            String roomId = String.valueOf(gameId);

            GameLogic logic = gameLogics.get(roomId);
            if (logic == null) {
                ack.sendAckData(Map.of("message", "error", "data", "ERROR_GAME_INVALID_ID"));
                return;
            }

            int playerId = findPlayerIdForClient(roomId, client);
            String abilityName = (String) map.get("ability");

            @SuppressWarnings("unchecked")
            Map<String, Object> targetMap = (Map<String, Object>) map.get("target");
            CardTarget target = objectMapper.convertValue(targetMap, CardTarget.class);

            UseAbilityAction action = new UseAbilityAction(playerId, abilityName, target);
            logic.dispatch(action);
            ack.sendAckData(Map.of("message", "ok"));
        } catch (Exception e) {
            System.err.println("Error processing ability: " + e.getMessage());
            ack.sendAckData(Map.of("message", "error", "data", e.getMessage()));
        }
    }

    @OnEvent("game:action:resolvePrompt")
    public void onPromptResponse(SocketIOClient client, AckRequest ack, Object data) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) data;
            int gameId = ((Number) map.get("gameId")).intValue();
            String roomId = String.valueOf(gameId);

            GameLogic logic = gameLogics.get(roomId);
            if (logic == null) {
                ack.sendAckData(Map.of("message", "error", "data", "ERROR_GAME_INVALID_ID"));
                return;
            }

            int promptId = ((Number) map.get("id")).intValue();
            Object result = map.get("result");

            ResolvePromptAction action = new ResolvePromptAction(promptId, result);
            logic.dispatch(action);
            ack.sendAckData(Map.of("message", "ok"));
        } catch (Exception e) {
            System.err.println("Error processing prompt response: " + e.getMessage());
            ack.sendAckData(Map.of("message", "error", "data", e.getMessage()));
        }
    }

    // === additional game actions (ack-expected) ===

    @OnEvent("game:action:play")
    public void onPlay(SocketIOClient client, AckRequest ack, Object data) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) data;
            int gameId = ((Number) map.get("gameId")).intValue();
            String roomId = String.valueOf(gameId);

            GameLogic logic = gameLogics.get(roomId);
            GameSession session = gameStore.getGame(roomId);
            if (logic == null || session == null) {
                ack.sendAckData(Map.of("message", "error", "data", "ERROR_GAME_INVALID_ID"));
                return;
            }

            @SuppressWarnings("unchecked")
            List<String> deck = (List<String>) map.get("deck");
            Integer actorClientId = client.get("clientId");
            Long actorUserId = client.get("userId");
            if (actorClientId == null || actorUserId == null) {
                ack.sendAckData(Map.of("message", "error", "data", "ERROR_NOT_AUTHENTICATED"));
                return;
            }

            User actorUser = userMapper.selectById(actorUserId);
            if (actorUser == null) {
                ack.sendAckData(Map.of("message", "error", "data", "ERROR_USER_NOT_FOUND"));
                return;
            }

            System.out.println("[onPlay] received from " + actorUser.getName()
                + " (clientId=" + actorClientId + ", userId=" + actorUserId + ") for Game " + roomId);
            System.out.println("[onPlay] deck=" + (deck != null ? deck.size() + " cards" : "NULL"));
            System.out.println("[onPlay] current state phase=" + logic.getState().getPhase()
                + ", players=" + logic.getState().getPlayers().size());

            // Match by userId (stable across reconnects) rather than clientId (changes on reconnect)
            State gameState = logic.getState();
            Player existingPlayer = gameState.getPlayers().stream()
                    .filter(p -> p.getUserId() == actorUserId)
                    .findFirst().orElse(null);

            if (existingPlayer != null) {
                System.out.println("[onPlay] " + actorUser.getName() + " already in game (playerId="
                    + existingPlayer.getId() + "), updating clientId to " + actorClientId);
                existingPlayer.setId(actorClientId);
            } else if (gameState.getPlayers().size() < 2) {
                System.out.println("[onPlay] adding " + actorUser.getName() + " as new player, dispatching AddPlayerAction");
                if (deck == null || deck.isEmpty()) {
                    System.err.println("[onPlay] ERROR: deck is null or empty for " + actorUser.getName());
                    ack.sendAckData(Map.of("message", "error", "data", "ERROR_DECK_REQUIRED"));
                    return;
                }
                logic.dispatch(new AddPlayerAction(actorClientId, actorUserId, actorUser.getName(), deck));
                System.out.println("[onPlay] dispatch returned, state phase=" + logic.getState().getPhase()
                    + ", players=" + logic.getState().getPlayers().size()
                    + ", prompts=" + logic.getState().getPrompts().size());
            } else {
                System.out.println("[onPlay] game full (" + gameState.getPlayers().size() + " players), cannot add " + actorUser.getName());
            }

            // Notify everyone in the lobby that the game info has updated
            server.getBroadcastOperations().sendEvent("core:gameInfo", buildGameInfo(session));

            ack.sendAckData(Map.of("message", "ok"));
        } catch (Exception e) {
            System.err.println("Error processing play: " + e.getMessage());
            ack.sendAckData(Map.of("message", "error", "data", e.getMessage()));
        }
    }

    @OnEvent("game:action:stadium")
    public void onStadium(SocketIOClient client, AckRequest ack, Object data) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) data;
            int gameId = ((Number) map.get("gameId")).intValue();
            String roomId = String.valueOf(gameId);

            GameLogic logic = gameLogics.get(roomId);
            if (logic == null) {
                ack.sendAckData(Map.of("message", "error", "data", "ERROR_GAME_INVALID_ID"));
                return;
            }

            int playerId = findPlayerIdForClient(roomId, client);
            logic.dispatch(new UseStadiumAction(playerId));
            ack.sendAckData(Map.of("message", "ok"));
        } catch (Exception e) {
            System.err.println("Error processing stadium: " + e.getMessage());
            ack.sendAckData(Map.of("message", "error", "data", e.getMessage()));
        }
    }

    @OnEvent("game:action:trainer")
    public void onTrainer(SocketIOClient client, AckRequest ack, Object data) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) data;
            int gameId = ((Number) map.get("gameId")).intValue();
            String roomId = String.valueOf(gameId);

            GameLogic logic = gameLogics.get(roomId);
            if (logic == null) {
                ack.sendAckData(Map.of("message", "error", "data", "ERROR_GAME_INVALID_ID"));
                return;
            }

            int playerId = findPlayerIdForClient(roomId, client);
            String cardName = (String) map.get("cardName");
            @SuppressWarnings("unchecked")
            Map<String, Object> targetMap = (Map<String, Object>) map.get("target");
            CardTarget target = objectMapper.convertValue(targetMap, CardTarget.class);

            logic.dispatch(new UseTrainerInPlayAction(playerId, target, cardName));
            ack.sendAckData(Map.of("message", "ok"));
        } catch (Exception e) {
            System.err.println("Error processing trainer: " + e.getMessage());
            ack.sendAckData(Map.of("message", "error", "data", e.getMessage()));
        }
    }

    @OnEvent("game:action:reorderBench")
    public void onReorderBench(SocketIOClient client, AckRequest ack, Object data) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) data;
            int gameId = ((Number) map.get("gameId")).intValue();
            String roomId = String.valueOf(gameId);

            GameLogic logic = gameLogics.get(roomId);
            if (logic == null) {
                ack.sendAckData(Map.of("message", "error", "data", "ERROR_GAME_INVALID_ID"));
                return;
            }

            int playerId = findPlayerIdForClient(roomId, client);
            int from = ((Number) map.get("from")).intValue();
            int to = ((Number) map.get("to")).intValue();
            logic.dispatch(new ReorderBenchAction(playerId, from, to));
            ack.sendAckData(Map.of("message", "ok"));
        } catch (Exception e) {
            System.err.println("Error processing reorderBench: " + e.getMessage());
            ack.sendAckData(Map.of("message", "error", "data", e.getMessage()));
        }
    }

    @OnEvent("game:action:reorderHand")
    public void onReorderHand(SocketIOClient client, AckRequest ack, Object data) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) data;
            int gameId = ((Number) map.get("gameId")).intValue();
            String roomId = String.valueOf(gameId);

            GameLogic logic = gameLogics.get(roomId);
            if (logic == null) {
                ack.sendAckData(Map.of("message", "error", "data", "ERROR_GAME_INVALID_ID"));
                return;
            }

            int playerId = findPlayerIdForClient(roomId, client);
            @SuppressWarnings("unchecked")
            List<Integer> order = (List<Integer>) map.get("order");
            logic.dispatch(new ReorderHandAction(playerId, order));
            ack.sendAckData(Map.of("message", "ok"));
        } catch (Exception e) {
            System.err.println("Error processing reorderHand: " + e.getMessage());
            ack.sendAckData(Map.of("message", "error", "data", e.getMessage()));
        }
    }

    @OnEvent("game:action:appendLog")
    public void onAppendLog(SocketIOClient client, AckRequest ack, Object data) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) data;
            int gameId = ((Number) map.get("gameId")).intValue();
            String roomId = String.valueOf(gameId);

            GameLogic logic = gameLogics.get(roomId);
            if (logic == null) {
                ack.sendAckData(Map.of("message", "error", "data", "ERROR_GAME_INVALID_ID"));
                return;
            }

            int playerId = findPlayerIdForClient(roomId, client);
            String message = (String) map.get("message");
            logic.dispatch(new AppendLogAction(playerId, "LOG_TEXT", Map.of("text", message)));
            ack.sendAckData(Map.of("message", "ok"));
        } catch (Exception e) {
            System.err.println("Error processing appendLog: " + e.getMessage());
            ack.sendAckData(Map.of("message", "error", "data", e.getMessage()));
        }
    }

    @OnEvent("game:action:changeAvatar")
    public void onChangeAvatar(SocketIOClient client, AckRequest ack, Object data) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) data;
            int gameId = ((Number) map.get("gameId")).intValue();
            String roomId = String.valueOf(gameId);

            GameLogic logic = gameLogics.get(roomId);
            if (logic == null) {
                ack.sendAckData(Map.of("message", "error", "data", "ERROR_GAME_INVALID_ID"));
                return;
            }

            int playerId = findPlayerIdForClient(roomId, client);
            String avatarName = (String) map.get("avatarName");
            logic.dispatch(new ChangeAvatarAction(playerId, avatarName));
            ack.sendAckData(Map.of("message", "ok"));
        } catch (Exception e) {
            System.err.println("Error processing changeAvatar: " + e.getMessage());
            ack.sendAckData(Map.of("message", "error", "data", e.getMessage()));
        }
    }

    // === GameStoreAdapter ===

    @Override
    public void onStateChange(State state) {
        for (Map.Entry<String, GameLogic> entry : gameLogics.entrySet()) {
            if (entry.getValue().getState() == state) {
                broadcastState(entry.getKey(), state);
                break;
            }
        }
    }

    // === private helpers ===

    private void broadcastState(String gameId, State state) {
        // Don't broadcast intermediate states during arbiter resolution (matches TypeScript)
        if (resolvingArbiter.contains(gameId)) {
            return;
        }

        try {
            // Resolve all arbiter prompts in a loop before any broadcast
            resolveArbiterPrompts(gameId, state);
            state = gameLogics.get(gameId).getState();

            System.out.println("[broadcastState] gameId=" + gameId + ", phase=" + state.getPhase()
                + ", players=" + state.getPlayers().size() + ", prompts=" + state.getPrompts().size()
                + ", activePlayer=" + state.getActivePlayerIndex());

            // Filter out resolved prompts before sending to client (matching TypeScript sanitizer)
            List<GamePrompt<?>> allPrompts = new ArrayList<>(state.getPrompts());
            List<GamePrompt<?>> unresolved = allPrompts.stream()
                    .filter(p -> p.getResult() == null)
                    .toList();
            state.getPrompts().clear();
            state.getPrompts().addAll(unresolved);

            try {
                String json = stateSerializer.serialize(state);
                String base64State = Base64.getEncoder().encodeToString(json.getBytes());
                server.getRoomOperations(gameId).sendEvent(
                        "game[" + gameId + "]:stateChange", Map.of(
                                "stateData", base64State,
                                "playerStats", List.of()
                        ));
            } finally {
                // Always restore all prompts for server-side logic
                state.getPrompts().clear();
                state.getPrompts().addAll(allPrompts);
            }
        } catch (Exception e) {
            System.err.println("Failed to broadcast state: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void resolveArbiterPrompts(String gameId, State state) {
        GameLogic logic = gameLogics.get(gameId);
        if (logic == null) return;

        resolvingArbiter.add(gameId);
        try {
            boolean resolved;
            do {
                resolved = false;
                for (GamePrompt<?> prompt : state.getPrompts()) {
                    if (prompt.getResult() != null) continue;
                    ResolvePromptAction resolution = GameArbiter.resolvePrompt(state, prompt);
                    if (resolution != null) {
                        System.out.println("[resolveArbiter] auto-resolving prompt id=" + prompt.getId()
                            + " type=" + prompt.getType());
                        logic.dispatch(resolution);
                        resolved = true;
                        break;
                    }
                }
            } while (resolved);
        } finally {
            resolvingArbiter.remove(gameId);
        }
    }

    private int extractGameId(Object data) {
        if (data instanceof Integer i) return i;
        if (data instanceof Number n) return n.intValue();
        if (data instanceof String s) {
            try { return Integer.parseInt(s); } catch (NumberFormatException e) { return 0; }
        }
        if (data instanceof Map<?, ?> map) {
            Object id = map.get("gameId");
            if (id instanceof Number n) return n.intValue();
            if (id instanceof String s) {
                try { return Integer.parseInt(s); } catch (NumberFormatException e) { return 0; }
            }
        }
        return 0;
    }

    private int findPlayerIdForClient(String gameId, SocketIOClient client) {
        Integer cId = client.get("clientId");
        if (cId != null) {
            return cId;
        }
        return -1;
    }

    private Map<String, Object> buildGameState(String gameId, State state, GameSession session) {
        try {
            String json = stateSerializer.serialize(state);
            System.out.println("[buildGameState] gameId=" + gameId + ", json length=" + json.length());
            if (json.length() < 2000) {
                System.out.println("[buildGameState] json=" + json);
            } else {
                System.out.println("[buildGameState] json start=" + json.substring(0, Math.min(500, json.length())));
            }
            String stateData = Base64.getEncoder().encodeToString(json.getBytes());
            Map<String, Object> gameState = new LinkedHashMap<>();
            gameState.put("gameId", Integer.parseInt(gameId));
            gameState.put("stateData", stateData);

            List<Integer> clientIds = new ArrayList<>();
            if (session.getPlayer1Client() != null) {
                Integer cId = session.getPlayer1Client().get("clientId");
                if (cId != null) clientIds.add(cId);
            }
            if (session.getPlayer2Client() != null) {
                Integer cId = session.getPlayer2Client().get("clientId");
                if (cId != null) clientIds.add(cId);
            }

            gameState.put("clientIds", clientIds);
            gameState.put("recordingEnabled", false);
            gameState.put("timeLimit", 600);
            gameState.put("playerStats", List.of());
            return gameState;
        } catch (Exception e) {
            System.err.println("Failed to build game state: " + e.getMessage());
            e.printStackTrace();
            return Map.of("gameId", Integer.parseInt(gameId));
        }
    }

    private Map<String, Object> buildUserInfo(User user, boolean connected) {
        Map<String, Object> userInfo = new LinkedHashMap<>();
        userInfo.put("connected", connected);
        userInfo.put("userId", user.getId());
        userInfo.put("name", user.getName());
        userInfo.put("email", user.getEmail());
        userInfo.put("ranking", user.getRanking());

        String rank = "JUNIOR";
        if (user.getRanking() >= 3000) {
            rank = "MASTER";
        } else if (user.getRanking() >= 1000) {
            rank = "SENIOR";
        }
        userInfo.put("rank", rank);

        userInfo.put("registered", user.getRegistered());
        userInfo.put("lastSeen", user.getLastSeen());
        userInfo.put("lastRankingChange", user.getLastRankingChange());
        userInfo.put("avatarFile", user.getAvatarFile() != null ? user.getAvatarFile() : "");
        return userInfo;
    }

    private Map<String, Object> buildPlayerInfo(Player player, GameSession session) {
        Map<String, Object> playerInfo = new LinkedHashMap<>();
        playerInfo.put("clientId", player.getId());
        playerInfo.put("name", player.getName());
        playerInfo.put("prizes", player.getPrizeLeft());
        playerInfo.put("deck", player.getDeck() != null && player.getDeck().getCards() != null ? player.getDeck().getCards().size() : 0);
        return playerInfo;
    }

    private Map<String, Object> buildGameInfo(GameSession session) {
        State state = session.getState();
        Map<String, Object> gameInfo = new LinkedHashMap<>();
        gameInfo.put("gameId", Integer.parseInt(session.getGameId()));
        gameInfo.put("phase", state != null ? state.getPhase().toString() : "WAITING_FOR_PLAYERS");
        gameInfo.put("turn", state != null ? state.getTurn() : 0);
        gameInfo.put("activePlayer", state != null ? state.getActivePlayerIndex() : 0);
        gameInfo.put("formatName", state != null && state.getRules() != null && state.getRules().getFormatName() != null ? state.getRules().getFormatName() : "");

        List<Map<String, Object>> players = new ArrayList<>();
        if (state != null) {
            for (Player p : state.getPlayers()) {
                players.add(buildPlayerInfo(p, session));
            }
        } else {
            if (session.getPlayer1Client() != null) {
                Map<String, Object> p1 = new LinkedHashMap<>();
                Integer cId = session.getPlayer1Client().get("clientId");
                p1.put("clientId", cId != null ? cId : 0);
                p1.put("name", "Player 1");
                p1.put("prizes", 0);
                p1.put("deck", 0);
                players.add(p1);
            }
            if (session.getPlayer2Client() != null) {
                Map<String, Object> p2 = new LinkedHashMap<>();
                Integer cId = session.getPlayer2Client().get("clientId");
                p2.put("clientId", cId != null ? cId : 0);
                p2.put("name", "Player 2");
                p2.put("prizes", 0);
                p2.put("deck", 0);
                players.add(p2);
            }
        }
        gameInfo.put("players", players);
        return gameInfo;
    }
}
