package com.ptcg.server.model.action;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RetreatAction implements GameAction {
    private final String type = "RETREAT_ACTION";
    private int playerId;
    private int benchIndex;
}
