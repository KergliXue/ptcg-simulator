package com.ptcg.server.model.card.set.cs4a;

import com.ptcg.server.engine.GameLogic;
import com.ptcg.server.factory.CardDefinition;
import com.ptcg.server.model.card.basic.*;
import com.ptcg.server.model.effect.*;
import com.ptcg.server.model.prompt.SelectPrompt;
import com.ptcg.server.model.state.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Collectors;

@CardDefinition(set = "CSV2C", name = "Ralts", cardKey = "CSV2C-Ralts")
public class Ralts extends PokemonCard {

    public Ralts() {
        setStage(Stage.BASIC);
        setCardTypes(Collections.singletonList(CardType.PSYCHIC));
        setHp(60);

        Weakness w = new Weakness();
        w.setType(CardType.DARKNESS);
        setWeakness(Collections.singletonList(w));

        setRetreat(Collections.singletonList(CardType.COLORLESS));

        Attack attack1 = new Attack();
        attack1.setName("Memory Skip");
        attack1.setCost(Collections.singletonList(CardType.PSYCHIC));
        attack1.setDamage("10");
        attack1.setText("Choose 1 of the Defending Pokémon's attacks. During your opponent's next turn, that Pokémon can't use that attack.");

        setAttacks(Collections.singletonList(attack1));
    }

    @Override
    public State reduceEffect(GameLogic logic, State state, GameEffect effect) {
        // Memory Skip: seal one of the opponent's attacks
        if (effect instanceof AttackEffect atkEffect
                && atkEffect.getAttack().getName().equals("Memory Skip")) {
            Player opponent = atkEffect.getOpponent();
            PokemonSlot opponentActive = opponent.getActive();
            PokemonCard defending = opponentActive.getPokemonCard();

            if (defending == null || defending.getAttacks().isEmpty()) return state;

            // Build options from defending Pokémon's attacks
            var options = defending.getAttacks().stream()
                    .map(a -> (Map<String, Object>) new java.util.HashMap<String, Object>(java.util.Map.of(
                            "attackName", a.getName(), "label", a.getName())))
                    .collect(Collectors.toList());

            logic.prompt(state, new SelectPrompt(atkEffect.getPlayer().getId(),
                    "Choose an attack to seal during your opponent's next turn", options),
                    choice -> {
                        if (choice != null && choice < options.size()) {
                            String sealedName = (String) options.get(choice).get("attackName");
                            // Place a marker on the opponent's active Pokémon
                            CardMarker sealMarker = new CardMarker("sealedAttack", this, sealedName, 1);
                            opponentActive.addMarker(sealMarker);
                            logic.log("Memory Skip: " + sealedName + " is sealed for the next turn", null);
                        }
                    });
        }

        return state;
    }
}
