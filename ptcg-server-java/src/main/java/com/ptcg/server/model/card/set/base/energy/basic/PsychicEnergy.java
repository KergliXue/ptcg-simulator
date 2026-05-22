package com.ptcg.server.model.card.set.base.energy.basic;

import com.ptcg.server.factory.CardDefinition;
import com.ptcg.server.model.card.basic.*;

import java.util.Collections;

@CardDefinition(set = "Base", name = "Psychic Energy", cardKey = "Base-PsychicEnergy")
public class PsychicEnergy extends EnergyCard {

    public PsychicEnergy() {
        setEnergyType(EnergyType.BASIC);
        setProvides(Collections.singletonList(CardType.PSYCHIC));
    }
}
