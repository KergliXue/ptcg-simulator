package com.ptcg.server.model.card.set.base.trainer.item;

import com.ptcg.server.engine.GameLogic;
import com.ptcg.server.factory.CardDefinition;
import com.ptcg.server.model.card.basic.Card;
import com.ptcg.server.model.card.basic.SuperType;
import com.ptcg.server.model.card.basic.TrainerCard;
import com.ptcg.server.model.card.basic.TrainerType;
import com.ptcg.server.model.effect.GameEffect;
import com.ptcg.server.model.effect.PlayItemEffect;
import com.ptcg.server.model.prompt.ChooseCardsPrompt;
import com.ptcg.server.model.state.Player;
import com.ptcg.server.model.state.State;

import java.util.Map;

@CardDefinition(set = "Base", name = "Ultra Ball", cardKey = "Base-UltraBall")
public class UltraBall extends TrainerCard {

    public UltraBall() {
        setTrainerType(TrainerType.ITEM);
        setText("You can play this card only if you discard 2 other cards from your hand. Search your deck for a Pokémon, reveal it, and put it into your hand. Then, shuffle your deck.");
    }

    @Override
    public State reduceEffect(GameLogic logic, State state, GameEffect effect) {
        if (effect instanceof PlayItemEffect item && item.getTrainerCard() == this) {
            Player player = item.getPlayer();

            long otherCardsInHand = player.getHand().getCards().stream()
                    .filter(card -> card != this).count();
            if (otherCardsInHand < 2) {
                throw new IllegalStateException("Not enough cards in hand to discard, cannot use Ultra Ball");
            }

            int thisIndex = player.getHand().getCards().indexOf(this);
            ChooseCardsPrompt.Options discardOpts = new ChooseCardsPrompt.Options(2, 2);
            if (thisIndex >= 0) {
                discardOpts.getBlocked().add(thisIndex);
            }
            logic.prompt(state, new ChooseCardsPrompt(
                    player.getId(),
                    "Choose 2 cards to discard",
                    player.getHand(),
                    null,
                    discardOpts
            ), discardedCards -> {
                if (discardedCards != null && discardedCards.size() == 2) {
                    player.discardFromHand(discardedCards);

                    if (!player.getDeck().getCards().isEmpty()) {
                        logic.prompt(state, new ChooseCardsPrompt(
                                player.getId(),
                                "Choose a Pokémon from your deck",
                                player.getDeck(),
                                Map.of("superType", SuperType.POKEMON.ordinal()),
                                0, 1
                        ), selectedCards -> {
                            if (selectedCards != null && !selectedCards.isEmpty()) {
                                Card chosen = selectedCards.getFirst();
                                player.getDeck().remove(chosen);
                                player.getHand().getCards().add(chosen);
                            }
                            player.shuffleDeck();
                        });
                    } else {
                        player.shuffleDeck();
                    }
                }
            });
        }

        return state;
    }
}
