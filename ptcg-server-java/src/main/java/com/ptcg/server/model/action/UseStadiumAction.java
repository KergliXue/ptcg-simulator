package com.ptcg.server.model.action;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UseStadiumAction implements GameAction {
    private final String type = "USE_STADIUM";
    private int playerId;
}
