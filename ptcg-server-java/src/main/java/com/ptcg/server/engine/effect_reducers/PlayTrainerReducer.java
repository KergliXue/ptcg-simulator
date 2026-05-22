package com.ptcg.server.engine.effect_reducers;

import com.ptcg.server.engine.GameLogic;
import com.ptcg.server.model.effect.*;
import com.ptcg.server.model.state.State;

public class PlayTrainerReducer {

    public static State reduce(GameLogic logic, State state, GameEffect effect) {
        if (effect instanceof PlaySupporterEffect supporter) {
            // Move card from hand to supporter zone (discard at end of turn)
            supporter.getPlayer().getHand().moveCardTo(
                    supporter.getTrainerCard(),
                    supporter.getPlayer().getSupporter());
            effect.setPreventDefault(true);
        }
        if (effect instanceof PlayItemEffect item) {
            // Discard after use (TRAINER_EFFECT handles the card-specific logic)
            item.getPlayer().getDiscard().getCards().add(item.getTrainerCard());
            item.getPlayer().getHand().getCards().remove(item.getTrainerCard());
            effect.setPreventDefault(true);
        }
        if (effect instanceof PlayStadiumEffect stadium) {
            stadium.getPlayer().getHand().moveCardTo(
                    stadium.getTrainerCard(),
                    stadium.getPlayer().getStadium());
            // Remove any existing stadium card
            State finalState = state;
            state.getPlayers().forEach(p -> {
                if (p != stadium.getPlayer() && p.getStadium().getCards().size() > 0) {
                    var cards = new java.util.ArrayList<>(p.getStadium().getCards());
                    cards.forEach(c -> {
                        p.getStadium().moveCardTo(c, p.getDiscard());
                    });
                }
            });
            effect.setPreventDefault(true);
        }
        if (effect instanceof AttachPokemonToolEffect tool) {
            if (tool.getPlayer().getHand().remove(tool.getTrainerCard())) {
                tool.getTarget().getTrainers().add(tool.getTrainerCard());
            }
            effect.setPreventDefault(true);
        }
        return state;
    }
}
