package com.ptcg.server.model.card.set.base.trainer.item;

import com.ptcg.server.engine.GameLogic;
import com.ptcg.server.factory.CardDefinition;
import com.ptcg.server.model.card.basic.*;
import com.ptcg.server.model.effect.GameEffect;
import com.ptcg.server.model.effect.PlayItemEffect;
import com.ptcg.server.model.prompt.ChooseCardsPrompt;
import com.ptcg.server.model.state.Player;
import com.ptcg.server.model.state.PokemonSlot;
import com.ptcg.server.model.state.State;

import java.util.Map;

@CardDefinition(set = "Base", name = "Nest Ball", cardKey = "Base-NestBall")
public class NestBall extends TrainerCard {

    public NestBall() {
        setTrainerType(TrainerType.ITEM);
        setText("Search your deck for a Basic Pokémon and put it onto your Bench. Then, shuffle your deck.");
    }

    @Override
    public State reduceEffect(GameLogic logic, State state, GameEffect effect) {
        if (effect instanceof PlayItemEffect item && item.getTrainerCard() == this) {
            Player player = item.getPlayer();

            if (player.getDeck().getCards().isEmpty()) {
                throw new IllegalStateException("Deck is empty, cannot use Nest Ball");
            }

            boolean hasEmptyBench = player.getBench().stream()
                    .anyMatch(slot -> slot.getPokemons().isEmpty());
            if (!hasEmptyBench) {
                throw new IllegalStateException("Bench is full, cannot use Nest Ball");
            }

            logic.prompt(state, new ChooseCardsPrompt(
                    player.getId(),
                    "Choose a Basic Pokémon to put on your Bench",
                    player.getDeck(),
                    Map.of("superType", SuperType.POKEMON.ordinal(), "stage", Stage.BASIC.ordinal()),
                    1, 1
            ), selectedCards -> {
                if (selectedCards != null && !selectedCards.isEmpty()) {
                    PokemonCard basicPokemon = (PokemonCard) selectedCards.get(0);
                    for (int i = 0; i < player.getBench().size(); i++) {
                        PokemonSlot slot = player.getBench().get(i);
                        if (slot.getPokemons().isEmpty()) {
                            player.getDeck().remove(basicPokemon);
                            player.getBench().get(i).getPokemons().add(basicPokemon);
                            break;
                        }
                    }
                }
                player.shuffleDeck();
            });
        }

        return state;
    }
}
