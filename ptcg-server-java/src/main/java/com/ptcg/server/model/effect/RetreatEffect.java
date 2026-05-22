package com.ptcg.server.model.effect;

import com.ptcg.server.model.state.Player;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class RetreatEffect implements GameEffect {
    private final String type = EffectType.RETREAT;
    private boolean preventDefault = false;
    private Player player;
    private int benchIndex;

    public RetreatEffect(Player player, int benchIndex) {
        this.player = player;
        this.benchIndex = benchIndex;
    }
}
