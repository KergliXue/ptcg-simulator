package com.ptcg.server.model.effect;

import com.ptcg.server.model.card.basic.EnergyCard;
import com.ptcg.server.model.state.Player;
import com.ptcg.server.model.state.PokemonSlot;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class AttachEnergyEffect implements GameEffect {
    private final String type = EffectType.ATTACH_ENERGY;
    private boolean preventDefault = false;
    private Player player;
    private EnergyCard energyCard;
    private PokemonSlot target;

    public AttachEnergyEffect(Player player, EnergyCard energyCard, PokemonSlot target) {
        this.player = player;
        this.energyCard = energyCard;
        this.target = target;
    }
}
