package com.ptcg.server.model.action;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AttackAction implements GameAction {
    private final String type = "ATTACK_ACTION";
    private int playerId;
    private String attackName;
}
