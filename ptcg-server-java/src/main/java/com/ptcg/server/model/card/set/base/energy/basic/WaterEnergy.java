package com.ptcg.server.model.card.set.base.energy.basic;

import com.ptcg.server.factory.CardDefinition;
import com.ptcg.server.model.card.basic.CardType;
import com.ptcg.server.model.card.basic.EnergyCard;
import com.ptcg.server.model.card.basic.EnergyType;

import java.util.Collections;
@CardDefinition(set = "Base", name = "Water Energy", cardKey = "Base-WaterEnergy")
public class WaterEnergy extends EnergyCard {

    public WaterEnergy() {
        setEnergyType(EnergyType.BASIC);
        setProvides(Collections.singletonList(CardType.WATER));
    }
}
