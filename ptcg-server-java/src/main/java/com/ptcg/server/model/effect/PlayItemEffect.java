package com.ptcg.server.model.effect;

import com.ptcg.server.model.card.basic.TrainerCard;
import com.ptcg.server.model.state.Player;
import com.ptcg.server.model.state.PokemonSlot;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class PlayItemEffect implements GameEffect {
    private final String type = EffectType.PLAY_ITEM;
    private boolean preventDefault = false;
    private Player player;
    private TrainerCard trainerCard;
    private PokemonSlot target;

    public PlayItemEffect(Player player, TrainerCard trainerCard, PokemonSlot target) {
        this.player = player;
        this.trainerCard = trainerCard;
        this.target = target;
    }
}
