package com.ptcg.server.model.effect;

import com.ptcg.server.model.card.basic.PokemonCard;
import com.ptcg.server.model.state.Player;
import com.ptcg.server.model.state.PokemonSlot;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class PlayPokemonEffect implements GameEffect {
    private final String type = EffectType.PLAY_POKEMON;
    private boolean preventDefault = false;
    private Player player;
    private PokemonCard pokemonCard;
    private PokemonSlot target;

    public PlayPokemonEffect(Player player, PokemonCard pokemonCard, PokemonSlot target) {
        this.player = player;
        this.pokemonCard = pokemonCard;
        this.target = target;
    }
}
