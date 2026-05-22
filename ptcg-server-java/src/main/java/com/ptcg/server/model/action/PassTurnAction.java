package com.ptcg.server.model.action;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PassTurnAction implements GameAction {
    private final String type = "PASS_TURN";
    private int playerId;
}
