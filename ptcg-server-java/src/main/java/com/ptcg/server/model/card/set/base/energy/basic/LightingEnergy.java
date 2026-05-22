package com.ptcg.server.model.card.set.base.energy.basic;

import com.ptcg.server.factory.CardDefinition;
import com.ptcg.server.model.card.basic.CardType;
import com.ptcg.server.model.card.basic.EnergyCard;
import com.ptcg.server.model.card.basic.EnergyType;

import java.util.Collections;
@CardDefinition(set = "Base", name = "Lightning Energy", cardKey = "Base-LightningEnergy")
public class LightingEnergy extends EnergyCard {




    public LightingEnergy() {
        setEnergyType(EnergyType.BASIC);
        setProvides(Collections.singletonList(CardType.LIGHTNING));
    }
}
