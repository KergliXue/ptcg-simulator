package com.ptcg.server.model.action;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UseAbilityAction implements GameAction {
    private final String type = "USE_ABILITY_ACTION";
    private int playerId;
    private String abilityName;
    private CardTarget target;
}
