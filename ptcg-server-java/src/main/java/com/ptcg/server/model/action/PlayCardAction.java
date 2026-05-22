package com.ptcg.server.model.action;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlayCardAction implements GameAction {
    private final String type = "PLAY_CARD_ACTION";
    private int playerId;
    private int handIndex;
    private CardTarget target;
}
