package com.ptcg.server.model.effect;

import com.ptcg.server.model.card.basic.PokemonCard;
import com.ptcg.server.model.state.Player;
import com.ptcg.server.model.state.PokemonSlot;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EvolveEffect implements GameEffect {
    private final String type = EffectType.EVOLVE;
    private boolean preventDefault = false;
    private Player player;
    private PokemonSlot target;
    private PokemonCard pokemonCard;
}
