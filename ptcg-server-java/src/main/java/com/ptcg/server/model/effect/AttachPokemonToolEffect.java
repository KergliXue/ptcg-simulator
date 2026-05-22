package com.ptcg.server.model.effect;

import com.ptcg.server.model.card.basic.TrainerCard;
import com.ptcg.server.model.state.Player;
import com.ptcg.server.model.state.PokemonSlot;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class AttachPokemonToolEffect implements GameEffect {
    private final String type = EffectType.ATTACH_POKEMON_TOOL;
    private boolean preventDefault = false;
    private Player player;
    private TrainerCard trainerCard;
    private PokemonSlot target;

    public AttachPokemonToolEffect(Player player, TrainerCard trainerCard, PokemonSlot target) {
        this.player = player;
        this.trainerCard = trainerCard;
        this.target = target;
    }
}
