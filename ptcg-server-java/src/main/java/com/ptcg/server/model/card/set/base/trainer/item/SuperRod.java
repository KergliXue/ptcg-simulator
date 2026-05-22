package com.ptcg.server.model.card.set.base.trainer.item;

import com.ptcg.server.engine.GameLogic;
import com.ptcg.server.factory.CardDefinition;
import com.ptcg.server.model.card.basic.*;
import com.ptcg.server.model.effect.GameEffect;
import com.ptcg.server.model.effect.PlayItemEffect;
import com.ptcg.server.model.prompt.ChooseCardsPrompt;
import com.ptcg.server.model.state.Player;
import com.ptcg.server.model.state.State;

import java.util.List;
import java.util.Map;

@CardDefinition(set = "Base", name = "Super Rod", cardKey = "Base-SuperRod")
public class SuperRod extends TrainerCard {

    public SuperRod() {
        setTrainerType(TrainerType.ITEM);
        setText("Shuffle up to 3 in any combination of Pokémon and Basic Energy cards from your discard pile back into your deck.");
    }

    @Override
    public State reduceEffect(GameLogic logic, State state, GameEffect effect) {
        if (effect instanceof PlayItemEffect item && item.getTrainerCard() == this) {
            Player player = item.getPlayer();

            long validTargets = player.getDiscard().getCards().stream()
                    .filter(c -> c instanceof PokemonCard
                            || (c instanceof EnergyCard && ((EnergyCard) c).getEnergyType() == EnergyType.BASIC))
                    .count();

            if (validTargets == 0) {
                throw new IllegalStateException("No Pokémon or Basic Energy in discard, cannot use Super Rod");
            }

            int maxToChoose = Math.min(3, (int) validTargets);

            logic.prompt(state, new ChooseCardsPrompt(
                    player.getId(),
                    "Choose up to " + maxToChoose + " Pokémon or Basic Energy to shuffle back into deck",
                    player.getDiscard(),
                    List.of(
                            Map.of("superType", SuperType.POKEMON.ordinal()),
                            Map.of("superType", SuperType.ENERGY.ordinal(), "energyType", EnergyType.BASIC.ordinal())
                    ),
                    1, maxToChoose
            ), selectedCards -> {
                if (selectedCards != null && !selectedCards.isEmpty()) {
                    for (Card card : selectedCards) {
                        player.getDiscard().remove(card);
                        player.getDeck().getCards().add(card);
                    }
                    player.shuffleDeck();
                }
            });
        }

        return state;
    }
}
