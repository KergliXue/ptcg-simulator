package com.ptcg.server.model.card.set.cs4a;

import com.ptcg.server.engine.GameLogic;
import com.ptcg.server.factory.CardDefinition;
import com.ptcg.server.model.card.basic.*;
import com.ptcg.server.model.effect.*;
import com.ptcg.server.model.prompt.ChooseCardsPrompt;
import com.ptcg.server.model.state.*;
import java.util.Arrays;
import java.util.Collections;

@CardDefinition(set = "CSV2C", name = "Kirlia", cardKey = "CSV2C-Kirlia")
public class Kirlia extends PokemonCard {

    public Kirlia() {
        setStage(Stage.STAGE_1);
        setEvolvesFrom("Ralts");
        setCardTypes(Collections.singletonList(CardType.PSYCHIC));
        setHp(80);

        Weakness w = new Weakness();
        w.setType(CardType.METAL);
        setWeakness(Collections.singletonList(w));

        setRetreat(Collections.singletonList(CardType.COLORLESS));

        Power refinement = new Power();
        refinement.setName("Refinement");
        refinement.setPowerType(PowerType.ABILITY);
        refinement.setUseWhenInPlay(true);
        refinement.setText("You must discard a card from your hand in order to use this Ability. Once during your turn, you may draw 2 cards.");
        setPowers(Collections.singletonList(refinement));

        Attack attack1 = new Attack();
        attack1.setName("Slap");
        attack1.setCost(Arrays.asList(CardType.PSYCHIC, CardType.COLORLESS));
        attack1.setDamage("20");
        attack1.setText("");

        setAttacks(Collections.singletonList(attack1));
    }

    @Override
    public State reduceEffect(GameLogic logic, State state, GameEffect effect) {
        // Refinement: discard 1 card from hand to draw 2 cards
        if (effect instanceof UsePowerEffect powerEffect
                && powerEffect.getPower().getName().equals("Refinement")) {
            Player player = powerEffect.getPlayer();

            if (player.getHand().getCards().isEmpty()) {
                throw new IllegalStateException("No cards in hand to discard for Refinement");
            }

            logic.prompt(state, new ChooseCardsPrompt(player.getId(),
                    "Choose 1 card to discard for Refinement",
                    player.getHand(),
                    1, 1),
                    discarded -> {
                        if (discarded != null && !discarded.isEmpty()) {
                            player.discardFromHand(discarded.get(0));
                            player.drawCards(2);
                            logic.log("Refinement: discarded 1 card and drew 2", null);
                        }
                    });
        }

        return state;
    }
}
