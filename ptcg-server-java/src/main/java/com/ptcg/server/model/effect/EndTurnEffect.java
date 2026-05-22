package com.ptcg.server.model.effect;

import com.ptcg.server.model.state.Player;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class EndTurnEffect implements GameEffect {
    private final String type = EffectType.END_TURN;
    private boolean preventDefault = false;
    private Player player;

    public EndTurnEffect(Player player) {
        this.player = player;
    }
}
