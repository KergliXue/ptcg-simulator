package com.ptcg.server.model.card.set.base.energy.special;

import com.ptcg.server.engine.GameLogic;
import com.ptcg.server.factory.CardDefinition;
import com.ptcg.server.model.card.basic.CardType;
import com.ptcg.server.model.card.basic.EnergyCard;
import com.ptcg.server.model.card.basic.EnergyType;
import com.ptcg.server.model.effect.AttachEnergyEffect;
import com.ptcg.server.model.effect.GameEffect;
import com.ptcg.server.model.state.Player;
import com.ptcg.server.model.state.PokemonSlot;
import com.ptcg.server.model.state.State;

import java.util.Collections;

@CardDefinition(set = "Base", name = "Jet Energy", cardKey = "Base-JetEnergy")
public class JetEnergy extends EnergyCard {

    public JetEnergy() {
        setEnergyType(EnergyType.SPECIAL);
        setProvides(Collections.singletonList(CardType.COLORLESS));
    }

    @Override
    public State reduceEffect(GameLogic logic, State state, GameEffect effect) {
        if (effect instanceof AttachEnergyEffect attachEffect && attachEffect.getEnergyCard() == this) {
            Player player = attachEffect.getPlayer();
            PokemonSlot targetSlot = attachEffect.getTarget();

            // When you attach this card from your hand to 1 of your Benched Pokémon,
            // switch that Pokémon with your Active Pokémon.

            // 为什么要player.getHand().getCards().contains(this) 是因为jet energy的前提是“手牌附着”
            if (player.getHand().getCards().contains(this) && player.getBench().contains(targetSlot)) {
                player.switchPokemon(targetSlot);
                logic.log(player.getName() + " switched " + targetSlot.getPokemonCard().getName() + " to Active due to Jet Energy.", null);
            }
        }
        return state;
    }
}
