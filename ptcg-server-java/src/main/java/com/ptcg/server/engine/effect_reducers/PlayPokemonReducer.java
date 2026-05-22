package com.ptcg.server.engine.effect_reducers;

import com.ptcg.server.engine.GameLogic;
import com.ptcg.server.model.card.basic.PokemonCard;
import com.ptcg.server.model.card.basic.Stage;
import com.ptcg.server.model.effect.*;
import com.ptcg.server.model.state.*;

public class PlayPokemonReducer {

    public static State reduce(GameLogic logic, State state, GameEffect effect) {
        if (effect instanceof PlayPokemonEffect play) {
            Player player = play.getPlayer();
            PokemonCard card = play.getPokemonCard();
            PokemonSlot target = play.getTarget();

            if (card.getStage() == Stage.BASIC) {
                // Basic: place directly into slot
                if (target.getPokemons().isEmpty() && target != player.getActive()) {
                    if (player.getHand().remove(card)) {
                        target.getPokemons().add(card);
                    }
                    target.setPokemonPlayedTurn(state.getTurn());
                }
            } else {
                // Evolution: must evolve from correct pre-evolution
                PokemonCard current = target.getPokemonCard();
                if (current != null && current.getName().equals(card.getEvolvesFrom())) {
                    if (player.getHand().remove(card)) {
                        target.getPokemons().add(card);
                    }
                    target.clearEffects();
                    target.setPokemonPlayedTurn(state.getTurn());
                } else {
                    throw new IllegalStateException(
                            "Cannot evolve: " + card.getName() + " must evolve from " + card.getEvolvesFrom());
                }
            }
            effect.setPreventDefault(true);
        }
        return state;
    }
}
