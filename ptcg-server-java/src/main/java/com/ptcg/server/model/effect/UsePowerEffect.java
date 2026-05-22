package com.ptcg.server.model.effect;

import com.ptcg.server.model.card.basic.PokemonCard;
import com.ptcg.server.model.card.basic.Power;
import com.ptcg.server.model.state.Player;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class UsePowerEffect implements GameEffect {
    private final String type = EffectType.USE_POWER;
    private boolean preventDefault = false;
    private Player player;
    private Power power;
    private PokemonCard card;

    public UsePowerEffect(Player player, Power power, PokemonCard card) {
        this.player = player;
        this.power = power;
        this.card = card;
    }
}
