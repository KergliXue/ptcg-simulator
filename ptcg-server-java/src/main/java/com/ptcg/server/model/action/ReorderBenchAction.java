package com.ptcg.server.model.action;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReorderBenchAction implements GameAction {
    private final String type = "REORDER_BENCH";
    private int playerId;
    private int from;
    private int to;
}
