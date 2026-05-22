package com.ptcg.server.engine.effect_reducers;

import com.ptcg.server.engine.GameLogic;
import com.ptcg.server.model.effect.AttachEnergyEffect;
import com.ptcg.server.model.effect.GameEffect;
import com.ptcg.server.model.state.State;

public class PlayEnergyReducer {

    public static State reduce(GameLogic logic, State state, GameEffect effect) {
        if (effect instanceof AttachEnergyEffect attach) {
            // Move energy from hand to target slot
            if (attach.getPlayer().getHand().remove(attach.getEnergyCard())) {
                attach.getTarget().getEnergies().add(attach.getEnergyCard());
            }
            effect.setPreventDefault(true);
        }
        return state;
    }
}
