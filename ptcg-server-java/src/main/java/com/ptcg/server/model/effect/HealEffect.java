package com.ptcg.server.model.effect;

import com.ptcg.server.model.state.Player;
import com.ptcg.server.model.state.PokemonSlot;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HealEffect implements GameEffect {
    private final String type = EffectType.HEAL;
    private boolean preventDefault = false;
    private Player player;
    private PokemonSlot target;
    private int damage;
}
