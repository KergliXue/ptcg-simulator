package com.ptcg.server.model.card.set.base.trainer.item;


import com.ptcg.server.engine.GameLogic;
import com.ptcg.server.factory.CardDefinition;
import com.ptcg.server.model.card.basic.TrainerCard;
import com.ptcg.server.model.card.basic.TrainerType;
import com.ptcg.server.model.effect.GameEffect;
import com.ptcg.server.model.effect.PlayItemEffect;
import com.ptcg.server.model.state.Player;
import com.ptcg.server.model.state.State;

@CardDefinition(set = "Base", name = "Pokemon Switch", cardKey = "Base-PokemonSwitch")
public class PokemonSwitch extends TrainerCard {

    public PokemonSwitch() {
        setTrainerType(TrainerType.ITEM);
        setText("Switch your Active Pokémon with 1 of your Benched Pokémon.\n");
    }

    @Override
    public State reduceEffect(GameLogic logic, State state, GameEffect effect) {
        if (effect instanceof PlayItemEffect item && item.getTrainerCard() == this) {
            Player player = item.getPlayer();
            // 如果没有备战区 那么是不能搞的
            if (player.getBench().isEmpty()) throw new IllegalStateException("No benched Pokemon,cannot use Switch");

        }


        return null;
    }
}
