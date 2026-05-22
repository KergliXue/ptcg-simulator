package com.ptcg.server.engine.effect_reducers;

import com.ptcg.server.engine.GameLogic;
import com.ptcg.server.model.card.basic.Card;
import com.ptcg.server.model.card.basic.PokemonCard;
import com.ptcg.server.model.effect.*;
import com.ptcg.server.model.prompt.ChooseCardsPrompt;
import com.ptcg.server.model.state.*;

import java.util.List;

public class CheckEffectReducer {

    public static State reduce(GameLogic logic, State state, GameEffect effect) {
        // Check KO for KnockOutEffect
        if (effect instanceof KnockOutEffect koEffect) {
            state = handleKnockOut(logic, state, koEffect.getPlayer(), koEffect.getTarget());
            effect.setPreventDefault(true);
        }
        return state;
    }

    public static State handleKnockOut(GameLogic logic, State state, Player owner, PokemonSlot slot) {
        // Move KO'd pokemon and all attached cards to discard
        slot.moveTo(owner.getDiscard());
        slot.clearEffects();
        logic.log("Knock Out: " + owner.getName() + "'s Pokemon", null);

        // Opponent takes prize cards
        int activeIdx = state.getActivePlayerIndex();
        Player opponent = state.getPlayers().get(activeIdx == 0 ? 1 : 0);
        int prizeCount = 1; // Default 1 prize
        // Check if KO'd card was an ex/GX/V etc (for 2 or 3 prize cards)
        if (slot.getPokemonCard() != null) {
            PokemonCard koCard = slot.getPokemonCard();
            if (koCard.getTags().contains("EX") || koCard.getTags().contains("GX")
                    || koCard.getTags().contains("VSTAR")) {
                prizeCount = 2;
            }
            if (koCard.getTags().contains("VMAX")) {
                prizeCount = 3;
            }
        }

        // Ask opponent to choose prize cards
        CardList availablePrizes = new CardList();
        for (CardList prize : opponent.getPrizes()) {
            availablePrizes.getCards().addAll(prize.getCards());
        }

        if (!opponent.getPrizes().isEmpty()) {
            ChooseCardsPrompt prizePrompt = new ChooseCardsPrompt(
                    opponent.getId(),
                    "Choose " + prizeCount + " prize card(s) to take",
                    availablePrizes,
                    prizeCount, prizeCount);

            logic.prompt(state, prizePrompt, chosenCards -> {
                if (chosenCards != null) {
                    for (Card card : chosenCards) {
                        for (CardList prize : opponent.getPrizes()) {
                            if (prize.getCards().remove(card)) {
                                opponent.getHand().getCards().add(card);
                                break;
                            }
                        }
                    }
                }
            });
        }

        // Check for win condition (no active pokemon, no bench pokemon, or all prizes taken)
        return checkWinCondition(logic, state, owner);
    }

    public static State checkState(GameLogic logic, State state) {
        // Only check win conditions during active gameplay phases
        if (state.getPhase() == GamePhase.WAITING_FOR_PLAYERS || state.getPhase() == GamePhase.SETUP) {
            return state;
        }
        // Check if any player has no active pokemon
        for (Player player : state.getPlayers()) {
            if (player.getActive().getPokemons().isEmpty()) {
                // Try to promote from bench
                boolean promoted = false;
                for (PokemonSlot bench : player.getBench()) {
                    if (!bench.getPokemons().isEmpty()) {
                        player.switchPokemon(bench);
                        logic.log(player.getName() + " promotes " +
                                bench.getPokemonCard().getName() + " to active", null);
                        promoted = true;
                        break;
                    }
                }
                if (!promoted) {
                    // No bench pokemon = lose
                    GameWinner winner = state.getPlayers().get(0) == player
                            ? GameWinner.PLAYER_2 : GameWinner.PLAYER_1;
                    return endGame(logic, state, winner);
                }
            }
        }
        return state;
    }

    private static State checkWinCondition(GameLogic logic, State state, Player knockedOutOwner) {
        // Check if knocked out player has any Pokemon left
        boolean hasPokemon = !knockedOutOwner.getActive().getPokemons().isEmpty();
        if (!hasPokemon) {
            for (PokemonSlot bench : knockedOutOwner.getBench()) {
                if (!bench.getPokemons().isEmpty()) {
                    hasPokemon = true;
                    break;
                }
            }
        }

        if (!hasPokemon) {
            GameWinner winner = state.getPlayers().get(0) == knockedOutOwner
                    ? GameWinner.PLAYER_2 : GameWinner.PLAYER_1;
            return endGame(logic, state, winner);
        }

        // Check if any player has 0 prize cards left
        for (Player player : state.getPlayers()) {
            if (player.getPrizeLeft() == 0) {
                GameWinner winner = state.getPlayers().get(0) == player
                        ? GameWinner.PLAYER_1 : GameWinner.PLAYER_2;
                return endGame(logic, state, winner);
            }
        }

        return state;
    }

    public static State endGame(GameLogic logic, State state, GameWinner winner) {
        state.setWinner(winner);
        state.setPhase(GamePhase.FINISHED);
        logic.log("Game Over! Winner: " + winner, null);
        return state;
    }
}
