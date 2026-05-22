package com.ptcg.server.engine.effect_reducers;

import com.ptcg.server.engine.GameLogic;
import com.ptcg.server.model.card.basic.CardType;
import com.ptcg.server.model.card.basic.PokemonCard;
import com.ptcg.server.model.card.basic.SpecialCondition;
import com.ptcg.server.model.effect.GameEffect;
import com.ptcg.server.model.effect.RetreatEffect;
import com.ptcg.server.model.state.*;

public class RetreatReducer {

    public static State reduce(GameLogic logic, State state, GameEffect effect) {
        if (effect instanceof RetreatEffect retreat) {
            Player player = retreat.getPlayer();
            int benchIndex = retreat.getBenchIndex();

            if (benchIndex < 0 || benchIndex >= player.getBench().size()) {
                throw new IllegalStateException("Invalid bench index");
            }

            PokemonSlot benchSlot = player.getBench().get(benchIndex);
            if (benchSlot.getPokemons().isEmpty()) {
                throw new IllegalStateException("Bench slot is empty");
            }

            // Can't retreat if paralyzed or asleep
            if (player.getActive().getSpecialConditions().contains(SpecialCondition.PARALYZED)
                    || player.getActive().getSpecialConditions().contains(SpecialCondition.ASLEEP)) {
                throw new IllegalStateException("Cannot retreat: special condition");
            }

            // Check if player already retreated this turn
            if (player.getRetreatedTurn() == state.getTurn()) {
                throw new IllegalStateException("Already retreated this turn");
            }

            // Pay retreat cost: discard energies equal to retreat cost
            PokemonCard activeCard = player.getActive().getPokemonCard();
            if (activeCard != null) {
                int retreatCost = activeCard.getRetreat().size();
                int paid = 0;
                var energies = new java.util.ArrayList<>(player.getActive().getEnergies());
                for (var energy : energies) {
                    if (paid >= retreatCost) break;
                    player.getActive().getEnergies().remove(energy);
                    player.getDiscard().add(energy);
                    paid++;
                }
                if (paid < retreatCost) {
                    throw new IllegalStateException("Cannot pay retreat cost");
                }
            }

            // Switch active with bench
            player.switchPokemon(benchSlot);
            player.setRetreatedTurn(state.getTurn());
            logic.log(player.getName() + " retreated to bench slot " + benchIndex, null);
            effect.setPreventDefault(true);
        }
        return state;
    }
}
