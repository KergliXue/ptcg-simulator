package com.ptcg.server.model.effect;

import com.ptcg.server.model.card.basic.Attack;
import com.ptcg.server.model.state.Player;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class UseAttackEffect implements GameEffect {
    private final String type = EffectType.USE_ATTACK;
    private boolean preventDefault = false;
    private Player player;
    private Attack attack;

    public UseAttackEffect(Player player, Attack attack) {
        this.player = player;
        this.attack = attack;
    }
}
