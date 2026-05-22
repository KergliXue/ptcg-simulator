package com.ptcg.server.model.card.basic;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class EnergyCard extends Card {

    private EnergyType energyType = EnergyType.BASIC;
    private List<CardType> provides = new ArrayList<>();
    private String text = "";

    @Override
    public SuperType getSuperType() {
        return SuperType.ENERGY;
    }
}
