package com.ptcg.server.model.effect;

import com.ptcg.server.model.state.Player;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BetweenTurnsEffect implements GameEffect {
    private final String type = EffectType.BETWEEN_TURNS;
    private boolean preventDefault = false;
    private Player player;
    private int poisonDamage;
    private int burnDamage;
    private Boolean burnFlipResult;
    private Boolean asleepFlipResult;

    public BetweenTurnsEffect(Player player) {
        this.player = player;
        this.poisonDamage = player.getActive().getPoisonDamage();
        this.burnDamage = player.getActive().getBurnDamage();
    }
}
