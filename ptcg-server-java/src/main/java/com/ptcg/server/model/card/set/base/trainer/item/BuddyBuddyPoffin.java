package com.ptcg.server.model.card.set.base.trainer.item;

import com.ptcg.server.engine.GameLogic;
import com.ptcg.server.factory.CardDefinition;
import com.ptcg.server.model.card.basic.*;
import com.ptcg.server.model.effect.GameEffect;
import com.ptcg.server.model.effect.PlayItemEffect;
import com.ptcg.server.model.prompt.ChooseCardsPrompt;
import com.ptcg.server.model.state.Player;
import com.ptcg.server.model.state.State;

import java.util.Map;

@CardDefinition(set = "Base", name = "Buddy-Buddy Poffin", cardKey = "Base-BuddyBuddyPoffin")
public class BuddyBuddyPoffin extends TrainerCard {

    public BuddyBuddyPoffin() {
        setTrainerType(TrainerType.ITEM);
        setText("Search your deck for up to 2 Basic Pokémon with 70 HP or less and put them onto your Bench. Then, shuffle your deck.");
    }

    @Override
    public State reduceEffect(GameLogic logic, State state, GameEffect effect) {
        if (effect instanceof PlayItemEffect item && item.getTrainerCard() == this) {
            Player player = item.getPlayer();

            if (player.getDeck().getCards().isEmpty()) {
                throw new IllegalStateException("Deck is empty, cannot use Buddy-Buddy Poffin");
            }

            int emptyBenchSlots = (int) player.getBench().stream()
                    .filter(slot -> slot.getPokemons().isEmpty())
                    .count();

            if (emptyBenchSlots == 0) {
                throw new IllegalStateException("Bench is full, cannot use Buddy-Buddy Poffin");
            }

            int maxToChoose = Math.min(2, emptyBenchSlots);

            logic.prompt(state, new ChooseCardsPrompt(
                    player.getId(),
                    "Choose up to " + maxToChoose + " Basic Pokémon with HP <= 70 to put on Bench",
                    player.getDeck(),
                    Map.of("superType", SuperType.POKEMON.ordinal(), "stage", Stage.BASIC.ordinal()),
                    0, maxToChoose
            ), selectedCards -> {
                if (selectedCards != null) {
                    int benchIdx = 0;
                    for (Card card : selectedCards) {
                        PokemonCard basicPokemon = (PokemonCard) card;
                        // Server-side HP validation (HP <= 70 can't be expressed in the serializable filter)
                        if (basicPokemon.getHp() > 70) continue;
                        while (benchIdx < player.getBench().size()
                                && !player.getBench().get(benchIdx).getPokemons().isEmpty()) {
                            benchIdx++;
                        }
                        if (benchIdx < player.getBench().size()) {
                            player.getDeck().remove(basicPokemon);
                            player.getBench().get(benchIdx).getPokemons().add(basicPokemon);
                            benchIdx++;
                        }
                    }
                }
                player.shuffleDeck();
            });
        }

        return state;
    }
}
