package com.ptcg.server.model.action;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AbortGameAction implements GameAction {
    private final String type = "ABORT_GAME_ACTION";
    private int playerId;
    private AbortReason reason;

    public enum AbortReason {
        DISCONNECTED,
        ILLEGAL_MOVES,
        TIME_ELAPSED,
        CONCEDED
    }
}
