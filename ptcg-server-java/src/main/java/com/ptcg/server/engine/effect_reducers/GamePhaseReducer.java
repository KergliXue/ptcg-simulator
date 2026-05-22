package com.ptcg.server.engine.effect_reducers;

import com.ptcg.server.engine.GameLogic;
import com.ptcg.server.model.card.basic.PokemonCard;
import com.ptcg.server.model.card.basic.SpecialCondition;
import com.ptcg.server.model.effect.*;
import com.ptcg.server.model.prompt.CoinFlipPrompt;
import com.ptcg.server.model.state.*;

public class GamePhaseReducer {

    public static State reduce(GameLogic logic, State state, GameEffect effect) {
        if (effect instanceof EndTurnEffect endTurn) {
            return handleEndTurn(logic, state, endTurn);
        }
        if (effect instanceof BetweenTurnsEffect betweenTurns) {
            return handleBetweenTurns(logic, state, betweenTurns);
        }
        return state;
    }

    private static State handleEndTurn(GameLogic logic, State state, EndTurnEffect effect) {
        Player player = effect.getPlayer();

        // Between turns: process special conditions
        BetweenTurnsEffect btEffect = new BetweenTurnsEffect(player);
        state = logic.reduceEffect(state, btEffect);

        // Switch to next player
        return initNextTurn(logic, state);
    }

    public static State initNextTurn(GameLogic logic, State state) {
        int nextPlayer = state.getActivePlayerIndex() == 0 ? 1 : 0;
        state.setActivePlayerIndex(nextPlayer);
        state.setTurn(state.getTurn() + 1);
        state.setPhase(GamePhase.PLAYER_TURN);

        Player player = state.getActivePlayer();
        if (player != null && !player.getDeck().getCards().isEmpty()) {
            player.drawCards(1);
        }

        // Decrement marker durations for all Pokémon slots
        for (Player p : state.getPlayers()) {
            java.util.List<PokemonSlot> slots = new java.util.ArrayList<>();
            slots.add(p.getActive());
            slots.addAll(p.getBench());
            for (PokemonSlot slot : slots) {
                var expired = slot.getMarker().stream()
                        .filter(m -> {
                            m.setDuration(m.getDuration() - 1);
                            return m.getDuration() <= 0;
                        })
                        .toList();
                slot.getMarker().removeAll(expired);
            }
        }

        return state;
    }

    private static State handleBetweenTurns(GameLogic logic, State state, BetweenTurnsEffect effect) {
        Player player = effect.getPlayer();
        PokemonSlot active = player.getActive();

        // Poison damage
        if (active.getSpecialConditions().contains(SpecialCondition.POISONED)) {
            active.setDamage(active.getDamage() + effect.getPoisonDamage());
            logic.log("Poison damage: " + player.getName() + " takes " + effect.getPoisonDamage(), null);
        }

        // Burn check + damage
        if (active.getSpecialConditions().contains(SpecialCondition.BURNED)) {
            CoinFlipPrompt burnFlip = new CoinFlipPrompt(player.getId(),
                    "Coin flip for burn: heads = no damage, tails = 20 damage");
            logic.prompt(state, burnFlip, result -> {
                if (Boolean.TRUE.equals(result)) {
                    logic.log("Burn flip: heads, no damage", null);
                } else {
                    active.setDamage(active.getDamage() + effect.getBurnDamage());
                    logic.log("Burn damage: " + player.getName() + " takes " + effect.getBurnDamage(), null);
                }
            });
        }

        // Asleep check
        if (active.getSpecialConditions().contains(SpecialCondition.ASLEEP)) {
            CoinFlipPrompt sleepFlip = new CoinFlipPrompt(player.getId(),
                    "Coin flip for sleep: heads = wake up, tails = still asleep");
            logic.prompt(state, sleepFlip, result -> {
                if (Boolean.TRUE.equals(result)) {
                    active.removeSpecialCondition(SpecialCondition.ASLEEP);
                    logic.log(player.getName() + " woke up!", null);
                }
            });
        }

        // Check for KO
        if (active.getDamage() >= getTotalHp(active)) {
            CheckEffectReducer.handleKnockOut(logic, state, player, active);
        }

        return state;
    }

    private static int getTotalHp(PokemonSlot slot) {
        PokemonCard card = slot.getPokemonCard();
        return card != null ? card.getHp() : 0;
    }
}
