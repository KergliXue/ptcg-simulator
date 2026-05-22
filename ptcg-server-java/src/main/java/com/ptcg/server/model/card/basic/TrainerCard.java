package com.ptcg.server.model.card.basic;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class TrainerCard extends Card {

    private TrainerType trainerType = TrainerType.ITEM;
    private String text = "";
    private boolean useWhenInPlay = false;

    @Override
    public SuperType getSuperType() {
        return SuperType.TRAINER;
    }
}
