package com.ptcg.server.engine.reducers;

import com.ptcg.server.engine.GameLogic;
import com.ptcg.server.engine.effect_reducers.GamePhaseReducer;
import com.ptcg.server.model.action.AddPlayerAction;
import com.ptcg.server.model.action.GameAction;
import com.ptcg.server.model.card.basic.Card;
import com.ptcg.server.model.card.basic.PokemonCard;
import com.ptcg.server.model.card.basic.Stage;
import com.ptcg.server.model.card.basic.SuperType;
import com.ptcg.server.model.prompt.ChooseCardsPrompt;
import com.ptcg.server.model.prompt.CoinFlipPrompt;
import com.ptcg.server.model.prompt.ShuffleDeckPrompt;
import com.ptcg.server.model.state.*;
import com.ptcg.server.model.action.PlayerType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class SetupReducer {

    public static State reduce(GameLogic logic, State state, GameAction action) {
        if (state.getPhase() != GamePhase.WAITING_FOR_PLAYERS) {
            return state;
        }

        if (action instanceof AddPlayerAction addAction) {
            System.out.println("[SetupReducer] AddPlayerAction: playerId=" + addAction.getPlayerId()
                + ", userId=" + addAction.getUserId() + ", name=" + addAction.getPlayerName()
                + ", deck=" + (addAction.getDeck() != null ? addAction.getDeck().size() + " cards" : "NULL"));
            if (state.getPlayers().size() >= 2) {
                throw new IllegalStateException("Max players reached");
            }

            Player player = createPlayer(addAction.getPlayerId(), addAction.getUserId(), addAction.getPlayerName());
            player.getDeck().setSecret(true);
            List<String> deckKeys = addAction.getDeck();
            System.out.println("[SetupReducer] creating " + deckKeys.size() + " cards for " + addAction.getPlayerName());
            for (String key : deckKeys) {
                Card card = com.ptcg.server.factory.CardFactory.createCard(key);
                state.getCardNames().add(card.getFullName());
                card.setId(state.getCardNames().size() - 1);
                player.getDeck().getCards().add(card);
            }
            System.out.println("[SetupReducer] deck built: " + player.getDeck().getCards().size() + " cards");

            state.getPlayers().add(player);
            System.out.println("[SetupReducer] players now: " + state.getPlayers().size());

            if (state.getPlayers().size() == 2) {
                System.out.println("[SetupReducer] 2 players — entering SETUP phase");
                state.setPhase(GamePhase.SETUP);
                return runSetup(logic, state);
            }
        }
        return state;
    }

    private static Player createPlayer(int id, long userId, String name) {
        Player player = new Player(id, name);
        player.setUserId(userId);
        for (int i = 0; i < 6; i++) {
            CardList prize = new CardList();
            prize.setSecret(true);
            player.getPrizes().set(i, prize);
        }
        for (int i = 0; i < 5; i++) {
            PokemonSlot bench = new PokemonSlot();
            player.getBench().set(i, bench);
        }
        player.getDiscard().setPublic(true);
        player.getStadium().setPublic(true);
        player.getSupporter().setPublic(true);
        return player;
    }

    /**
     * Setup order:
     *   1. Coin Flip (who goes first)
     *   2. Mulligan (draw 7, shuffle & redraw until both have basics)
     *   3. Choose Starting Pokemon (active + bench)
     *   4. initNextTurn (start PLAYER_TURN)
     */
    private static State runSetup(GameLogic logic, State state) {
        Player player = state.getPlayers().get(0);
        Player opponent = state.getPlayers().get(1);
        System.out.println("[runSetup] starting: player=" + player.getName() + "(id=" + player.getId()
            + ", userId=" + player.getUserId() + "), opponent=" + opponent.getName()
            + "(id=" + opponent.getId() + ", userId=" + opponent.getUserId() + ")");
        System.out.println("[runSetup] player deck=" + player.getDeck().getCards().size()
            + " cards, opponent deck=" + opponent.getDeck().getCards().size() + " cards");

        // Step 1: Coin flip to determine who goes first
        System.out.println("[runSetup] creating CoinFlipPrompt");
        CoinFlipPrompt coinFlip = new CoinFlipPrompt(player.getId(),
                "COIN_FLIP");
        logic.prompt(state, coinFlip, whoBegins -> {
            System.out.println("[runSetup] coin flip result=" + whoBegins + ", setting activePlayerIndex="
                + (Boolean.TRUE.equals(whoBegins) ? 0 : 1));
            state.setActivePlayerIndex(Boolean.TRUE.equals(whoBegins) ? 0 : 1);

            // Step 2: Mulligan — draw 7, shuffle & redraw until both have basics
            doMulligan(logic, state, player, opponent);
        });

        return state;
    }

    private static void doMulligan(GameLogic logic, State state, Player player, Player opponent) {
        System.out.println("[doMulligan] starting for " + player.getName() + " and " + opponent.getName());
        Predicate<Card> basicPokemon = card ->
                card instanceof PokemonCard && ((PokemonCard) card).getStage() == Stage.BASIC;

        player.getHand().getCards().clear();
        opponent.getHand().getCards().clear();
        System.out.println("[doMulligan] hands cleared, deck sizes: player="
            + player.getDeck().getCards().size() + ", opponent=" + opponent.getDeck().getCards().size());

        boolean[] playerHasBasic = {false};
        boolean[] opponentHasBasic = {false};

        Runnable trySetup = new Runnable() {
            @Override
            public void run() {
                System.out.println("[doMulligan] trySetup: playerHasBasic=" + playerHasBasic[0]
                    + ", opponentHasBasic=" + opponentHasBasic[0]);
                if (!playerHasBasic[0]) {
                    System.out.println("[doMulligan] " + player.getName() + " needs mulligan, hand="
                        + player.getHand().getCards().size() + ", deck=" + player.getDeck().getCards().size());
                    player.getHand().moveTo(player.getDeck());
                    ShuffleDeckPrompt shufflePrompt = new ShuffleDeckPrompt(player.getId());
                    logic.prompt(state, shufflePrompt, order -> {
                        player.getDeck().applyOrder(order);
                        player.getDeck().moveTo(player.getHand(), 7);
                        playerHasBasic[0] = player.getHand().getCards().stream().anyMatch(basicPokemon);
                        System.out.println("[doMulligan] " + player.getName() + " drew 7, hasBasic="
                            + playerHasBasic[0] + ", hand=" + player.getHand().getCards().size()
                            + ", deck=" + player.getDeck().getCards().size());
                        this.run();
                    });
                    return;
                }
                if (!opponentHasBasic[0]) {
                    System.out.println("[doMulligan] " + opponent.getName() + " needs mulligan, hand="
                        + opponent.getHand().getCards().size() + ", deck=" + opponent.getDeck().getCards().size());
                    opponent.getHand().moveTo(opponent.getDeck());
                    ShuffleDeckPrompt shufflePrompt = new ShuffleDeckPrompt(opponent.getId());
                    logic.prompt(state, shufflePrompt, order -> {
                        opponent.getDeck().applyOrder(order);
                        opponent.getDeck().moveTo(opponent.getHand(), 7);
                        opponentHasBasic[0] = opponent.getHand().getCards().stream().anyMatch(basicPokemon);
                        System.out.println("[doMulligan] " + opponent.getName() + " drew 7, hasBasic="
                            + opponentHasBasic[0] + ", hand=" + opponent.getHand().getCards().size()
                            + ", deck=" + opponent.getDeck().getCards().size());
                        this.run();
                    });
                    return;
                }

                // Both have basics → proceed to choose starting Pokemon
                System.out.println("[doMulligan] both have basics! calling chooseStartingPokemons");
                chooseStartingPokemons(logic, state, player, opponent);
            }
        };
        trySetup.run();
    }

    private static void chooseStartingPokemons(GameLogic logic, State state,
                                               Player player, Player opponent) {
        System.out.println("[chooseStartingPokemons] creating prompts for both players");
        Map<String, Object> basicFilter = new LinkedHashMap<>();
        basicFilter.put("superType", SuperType.POKEMON.ordinal());
        basicFilter.put("stage", Stage.BASIC.ordinal());

        ChooseCardsPrompt.Options cardOpts = new ChooseCardsPrompt.Options(1, 6);
        cardOpts.setAllowCancel(false);

        ChooseCardsPrompt playerPrompt = new ChooseCardsPrompt(
                player.getId(), "CHOOSE_STARTING_POKEMONS",
                player.getHand(), basicFilter, cardOpts);

        ChooseCardsPrompt opponentPrompt = new ChooseCardsPrompt(
                opponent.getId(), "CHOOSE_STARTING_POKEMONS",
                opponent.getHand(), basicFilter, cardOpts);

        boolean[] done = {false, false};

        Runnable tryBeginGame = () -> {
            if (done[0] && done[1]) {
                System.out.println("[chooseStartingPokemons] both players done, starting game");
                Player first = state.getPlayers().get(state.getActivePlayerIndex());
                Player second = state.getPlayers().get(state.getActivePlayerIndex() == 0 ? 1 : 0);
                first.forEachPokemon(PlayerType.BOTTOM_PLAYER, (slot, target) ->
                        slot.setPokemonPlayedTurn(1));
                second.forEachPokemon(PlayerType.TOP_PLAYER, (slot, target) ->
                        slot.setPokemonPlayedTurn(2));
                GamePhaseReducer.initNextTurn(logic, state);
                System.out.println("[chooseStartingPokemons] game started, phase=" + state.getPhase()
                    + ", turn=" + state.getTurn() + ", activePlayer=" + state.getActivePlayerIndex());
            }
        };

        logic.prompt(state, playerPrompt, playerCards -> {
            System.out.println("[chooseStartingPokemons] " + player.getName() + " chose "
                + (playerCards != null ? ((List<Card>) playerCards).size() : 0) + " cards");
            putStartingPokemonsAndPrizes(player, (List<Card>) playerCards);
            done[0] = true;
            tryBeginGame.run();
        });

        logic.prompt(state, opponentPrompt, opponentCards -> {
            System.out.println("[chooseStartingPokemons] " + opponent.getName() + " chose "
                + (opponentCards != null ? ((List<Card>) opponentCards).size() : 0) + " cards");
            putStartingPokemonsAndPrizes(opponent, (List<Card>) opponentCards);
            done[1] = true;
            tryBeginGame.run();
        });
    }

    @SuppressWarnings("unchecked")
    private static void putStartingPokemonsAndPrizes(Player player, List<Card> cards) {
        if (cards == null || cards.isEmpty()) return;
        // First card = active
        if (player.getHand().remove(cards.get(0))) {
            player.getActive().getPokemons().add((PokemonCard) cards.get(0));
        }
        // Remaining cards = bench
        for (int i = 1; i < cards.size(); i++) {
            if (player.getHand().remove(cards.get(i))) {
                player.getBench().get(i - 1).getPokemons().add((PokemonCard) cards.get(i));
            }
        }
        // Draw 6 prize cards
        for (int i = 0; i < 6; i++) {
            player.getDeck().moveTo(player.getPrizes().get(i), 1);
        }
    }
}
