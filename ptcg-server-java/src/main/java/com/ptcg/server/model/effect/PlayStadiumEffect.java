package com.ptcg.server.model.effect;

import com.ptcg.server.model.card.basic.TrainerCard;
import com.ptcg.server.model.state.Player;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class PlayStadiumEffect implements GameEffect {
    private final String type = EffectType.PLAY_STADIUM;
    private boolean preventDefault = false;
    private Player player;
    private TrainerCard trainerCard;

    public PlayStadiumEffect(Player player, TrainerCard trainerCard) {
        this.player = player;
        this.trainerCard = trainerCard;
    }
}
