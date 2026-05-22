package com.ptcg.server.store;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.ptcg.server.model.card.basic.Card;
import com.ptcg.server.model.prompt.ChooseCardsPrompt;
import com.ptcg.server.model.state.State;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Component
public class GameStoreImpl implements GameStore {

    private final SocketIOServer server;
    private final Map<String, GameSession> games = new ConcurrentHashMap<>();
    private final Map<String, Consumer<List<Card>>> pendingPrompts = new ConcurrentHashMap<>();

    public GameStoreImpl(SocketIOServer server) {
        this.server = server;
    }

    public Map<String, GameSession> getGames() {
        return games;
    }

    public GameSession createGame(String gameId, SocketIOClient player1, SocketIOClient player2) {
        GameSession session = new GameSession(gameId, player1, player2);
        games.put(gameId, session);
        return session;
    }

    public GameSession getGame(String gameId) {
        return games.get(gameId);
    }

    public void removeGame(String gameId) {
        games.remove(gameId);
    }

    @Override
    public void prompt(State state, Object prompt, Consumer<List<Card>> callback) {
        String promptId = UUID.randomUUID().toString();
        pendingPrompts.put(promptId, callback);

        if (prompt instanceof ChooseCardsPrompt) {
            ChooseCardsPrompt cp = (ChooseCardsPrompt) prompt;
            String gameId = findGameIdForPlayer(cp.getPlayerId());
            if (gameId != null) {
                GameSession session = games.get(gameId);
                if (session != null) {
                    SocketIOClient client = session.getClientForPlayer(cp.getPlayerId());
                    if (client != null) {
                        client.sendEvent("game:prompt", new PromptMessage(promptId, cp));
                    }
                }
            }
        }
    }

    public void resolvePrompt(String promptId, List<Card> selectedCards) {
        Consumer<List<Card>> callback = pendingPrompts.remove(promptId);
        if (callback != null) {
            callback.accept(selectedCards);
        }
    }

    private String findGameIdForPlayer(int playerId) {
        for (Map.Entry<String, GameSession> entry : games.entrySet()) {
            GameSession session = entry.getValue();
            if (session.getPlayer1Id() == playerId || session.getPlayer2Id() == playerId) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static class PromptMessage {
        public String promptId;
        public ChooseCardsPrompt prompt;

        public PromptMessage(String promptId, ChooseCardsPrompt prompt) {
            this.promptId = promptId;
            this.prompt = prompt;
        }
    }
}
