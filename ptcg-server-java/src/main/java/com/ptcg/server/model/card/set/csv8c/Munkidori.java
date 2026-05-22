package com.ptcg.server.model.card.set.csv8c;

import com.ptcg.server.engine.GameLogic;
import com.ptcg.server.factory.CardDefinition;
import com.ptcg.server.model.card.basic.*;
import com.ptcg.server.model.effect.*;
import com.ptcg.server.model.prompt.SelectPrompt;
import com.ptcg.server.model.state.*;
import java.util.Collections;

@CardDefinition(set = "CSV8C", name = "Munkidori", cardKey = "CSV8C-Munkidori")
public class Munkidori extends PokemonCard {

    public Munkidori() {
        setStage(Stage.BASIC);
        setCardTypes(Collections.singletonList(CardType.PSYCHIC));
        setHp(110);

        Weakness w = new Weakness();
        w.setType(CardType.DARKNESS);
        setWeakness(Collections.singletonList(w));

        Resistance r = new Resistance();
        r.setType(CardType.FIGHTING);
        r.setValue(30);
        setResistance(Collections.singletonList(r));

        setRetreat(Collections.singletonList(CardType.COLORLESS));

        // Ability: Adrena Brain
        Power adrenaBrain = new Power();
        adrenaBrain.setName("Adrena Brain");
        adrenaBrain.setPowerType(PowerType.ABILITY);
        adrenaBrain.setUseWhenInPlay(true);
        adrenaBrain.setText("Once during your turn, you may put 3 damage counters on 1 of your opponent's Pokémon. If you placed any damage counters in this way, your turn ends.");
        setPowers(Collections.singletonList(adrenaBrain));

        // Attack: Mind Bend
        Attack mindBend = new Attack();
        mindBend.setName("Mind Bend");
        mindBend.setCost(Collections.singletonList(CardType.PSYCHIC));
        mindBend.setDamage("");
        mindBend.setText("This attack does 10 damage for each damage counter on your opponent's Active Pokémon.");
        setAttacks(Collections.singletonList(mindBend));
    }

    @Override
    public State reduceEffect(GameLogic logic, State state, GameEffect effect) {
        // Mind Bend: damage = 10 per damage counter on opponent's active
        if (effect instanceof AttackEffect atkEffect
                && atkEffect.getAttack().getName().equals("Mind Bend")) {
            PokemonSlot opponentActive = atkEffect.getOpponent().getActive();
            atkEffect.setDamage(opponentActive.getDamage());
            logic.log("Mind Bend deals " + opponentActive.getDamage() + " damage", null);
        }

        // Adrena Brain: put 3 damage counters on 1 opponent's Pokémon, then end turn
        if (effect instanceof UsePowerEffect powerEffect
                && powerEffect.getPower().getName().equals("Adrena Brain")) {
            Player opponent = state.getOpponentPlayer();
            if (opponent == null) return state;

            // Build options list: opponent's active + occupied bench slots
            var options = new java.util.ArrayList<java.util.Map<String, Object>>();
            if (opponent.getActive().getPokemonCard() != null) {
                options.add(java.util.Map.of("slot", "active", "label", opponent.getName() + "'s Active"));
            }
            for (int i = 0; i < opponent.getBench().size(); i++) {
                if (opponent.getBench().get(i).getPokemonCard() != null) {
                    options.add(java.util.Map.of("slot", "bench", "index", i,
                            "label", opponent.getName() + "'s Bench " + (i + 1)));
                }
            }

            if (options.isEmpty()) return state;

            logic.prompt(state, new SelectPrompt(powerEffect.getPlayer().getId(),
                    "Choose an opponent's Pokémon to put 3 damage counters on", options),
                    choice -> {
                        if (choice != null) {
                            String slot = (String) options.get(choice).get("slot");
                            PokemonSlot target;
                            if ("active".equals(slot)) {
                                target = opponent.getActive();
                            } else {
                                int idx = (Integer) options.get(choice).get("index");
                                target = opponent.getBench().get(idx);
                            }
                            target.setDamage(target.getDamage() + 30);
                            logic.log("Adrena Brain: 3 damage counters placed on opponent's Pokémon", null);
                        }
                    });
        }

        return state;
    }
}
