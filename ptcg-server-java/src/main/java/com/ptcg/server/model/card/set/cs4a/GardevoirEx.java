package com.ptcg.server.model.card.set.cs4a;

import com.ptcg.server.engine.GameLogic;
import com.ptcg.server.factory.CardDefinition;
import com.ptcg.server.model.card.basic.*;
import com.ptcg.server.model.effect.*;
import com.ptcg.server.model.prompt.ChooseCardsPrompt;
import com.ptcg.server.model.prompt.SelectPrompt;
import com.ptcg.server.model.state.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

@CardDefinition(set = "CSV2C", name = "Gardevoir ex", cardKey = "CSV2C-GardevoirEx")
public class GardevoirEx extends PokemonCard {

    public GardevoirEx() {
        setStage(Stage.STAGE_2);
        setEvolvesFrom("Kirlia");
        setCardTypes(Collections.singletonList(CardType.PSYCHIC));
        setHp(310); // Gardevoir ex has 310 HP

        // Weakness: Darkness (朱紫系列的超能属性弱点多为恶)
        Weakness w = new Weakness();
        w.setType(CardType.DARKNESS);
        setWeakness(Collections.singletonList(w));

        // Retreat: 2 Colorless
        setRetreat(Arrays.asList(CardType.COLORLESS, CardType.COLORLESS));

        // Ability 特性：超能拥抱 (Psychic Embrace)
        Power psychicEmbrace = new Power();
        psychicEmbrace.setName("Psychic Embrace");
        psychicEmbrace.setPowerType(PowerType.ABILITY);
        psychicEmbrace.setUseWhenInPlay(true);
        psychicEmbrace.setText("As often as you like during your turn, you may attach a Basic Psychic Energy card from your discard pile to 1 of your Psychic Pokémon. If you attached Energy to a Pokémon in this way, put 2 damage counters on that Pokémon. You can't use this Ability on a Pokémon that would be Knocked Out.");
        setPowers(Collections.singletonList(psychicEmbrace));

        // Attack 招式：奇迹力 (Miracle Force)
        Attack attack1 = new Attack();
        attack1.setName("Miracle Force");
        attack1.setCost(Arrays.asList(CardType.PSYCHIC, CardType.PSYCHIC, CardType.COLORLESS));
        attack1.setDamage("190");
        attack1.setText("This Pokémon recovers from all Special Conditions.");

        setAttacks(Collections.singletonList(attack1));
    }

    @Override
    public State reduceEffect(GameLogic logic, State state, GameEffect effect) {
        // Miracle Force: recover from all special conditions
        if (effect instanceof AttackEffect atkEffect
                && atkEffect.getAttack().getName().equals("Miracle Force")) {
            atkEffect.getPlayer().getActive().getSpecialConditions().clear();
            logic.log("Miracle Force: all Special Conditions removed", null);
        }

        // Psychic Embrace: attach Psychic Energy from discard to a Psychic Pokémon
        if (effect instanceof UsePowerEffect powerEffect
                && powerEffect.getPower().getName().equals("Psychic Embrace")) {
            Player player = powerEffect.getPlayer();

            // Find Psychic Energy cards in discard
            var psychicEnergies = player.getDiscard().getCards().stream()
                    .filter(c -> c instanceof EnergyCard ec
                            && ec.getProvides().contains(CardType.PSYCHIC))
                    .toList();

            if (psychicEnergies.isEmpty()) {
                logic.log("No Psychic Energy in discard", null);
                return state;
            }

            // Build list of valid Psychic Pokémon targets (not KO'd)
            var targets = new java.util.ArrayList<java.util.Map<String, Object>>();
            PokemonSlot active = player.getActive();
            if (active.getPokemonCard() != null && active.getPokemonCard().getCardTypes().contains(CardType.PSYCHIC)
                    && active.getDamage() < active.getPokemonCard().getHp()) {
                targets.add(java.util.Map.of("slot", "active", "label", "Active"));
            }
            for (int i = 0; i < player.getBench().size(); i++) {
                PokemonSlot bench = player.getBench().get(i);
                if (bench.getPokemonCard() != null && bench.getPokemonCard().getCardTypes().contains(CardType.PSYCHIC)
                        && bench.getDamage() < bench.getPokemonCard().getHp()) {
                    targets.add(java.util.Map.of("slot", "bench", "index", i, "label", "Bench " + (i + 1)));
                }
            }

            if (targets.isEmpty()) {
                logic.log("No valid Psychic Pokémon target for Psychic Embrace", null);
                return state;
            }

            // Prompt: choose a Psychic Energy from discard
            logic.prompt(state, new ChooseCardsPrompt(player.getId(),
                    "Choose a Basic Psychic Energy from your discard pile",
                    player.getDiscard(),
                    Map.of("superType", SuperType.ENERGY.ordinal(),
                           "provides", java.util.List.of(CardType.PSYCHIC.ordinal())),
                    0, 1),
                    chosenEnergies -> {
                        if (chosenEnergies == null || chosenEnergies.isEmpty()) return;

                        EnergyCard energy = (EnergyCard) chosenEnergies.get(0);
                        player.getDiscard().remove(energy);

                        // Prompt: choose target Psychic Pokémon
                        logic.prompt(state, new SelectPrompt(player.getId(),
                                "Choose a Psychic Pokémon to attach energy to (2 damage counters will be placed)",
                                targets),
                                choice -> {
                                    if (choice == null) {
                                        player.getDiscard().add(energy);
                                        return;
                                    }
                                    String slot = (String) targets.get(choice).get("slot");
                                    PokemonSlot target;
                                    if ("active".equals(slot)) {
                                        target = player.getActive();
                                    } else {
                                        int idx = (Integer) targets.get(choice).get("index");
                                        target = player.getBench().get(idx);
                                    }
                                    target.getEnergies().add(energy);
                                    target.setDamage(target.getDamage() + 20);
                                    logic.log("Psychic Embrace: attached Psychic Energy and placed 2 damage counters", null);
                                });
                    });
        }

        return state;
    }
}
