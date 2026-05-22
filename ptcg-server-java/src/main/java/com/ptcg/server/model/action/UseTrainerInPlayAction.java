package com.ptcg.server.model.action;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UseTrainerInPlayAction implements GameAction {
    private final String type = "USE_TRAINER_IN_PLAY";
    private int playerId;
    private CardTarget target;
    private String cardName;
}
