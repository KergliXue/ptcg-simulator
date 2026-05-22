package com.ptcg.server.model.effect;

import com.ptcg.server.model.card.basic.Attack;
import com.ptcg.server.model.state.Player;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AttackEffect implements GameEffect {
    private final String type = EffectType.ATTACK;
    private boolean preventDefault = false;
    private Player player;
    private Player opponent;
    private Attack attack;
    private int damage;
    private boolean ignoreWeakness = false;
    private boolean ignoreResistance = false;

    public AttackEffect(Player player, Player opponent, Attack attack) {
        this.player = player;
        this.opponent = opponent;
        this.attack = attack;
        this.damage = parseDamage(attack.getDamage());
    }

    private int parseDamage(String damageStr) {
        if (damageStr == null || damageStr.isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(damageStr.replaceAll("\\D", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
