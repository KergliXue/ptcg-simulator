package com.ptcg.server.model.card.basic;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class PokemonCard extends Card {

    private List<CardType> cardTypes = new ArrayList<>();
    private String evolvesFrom = "";
    private Stage stage = Stage.BASIC;
    private List<CardType> retreat = new ArrayList<>();
    private int hp = 0;
    private List<Weakness> weakness = new ArrayList<>();
    private List<Resistance> resistance = new ArrayList<>();
    private List<Power> powers = new ArrayList<>();
    private List<Attack> attacks = new ArrayList<>();

    @Override
    public SuperType getSuperType() {
        return SuperType.POKEMON;
    }
}
