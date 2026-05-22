package com.ptcg.server.model.action;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReorderHandAction implements GameAction {
    private final String type = "REORDER_HAND";
    private int playerId;
    private List<Integer> order;
}
