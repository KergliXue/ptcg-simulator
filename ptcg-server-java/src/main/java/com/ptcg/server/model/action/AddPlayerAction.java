package com.ptcg.server.model.action;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AddPlayerAction implements GameAction {
    private final String type = "ADD_PLAYER_ACTION";
    private int playerId;
    private long userId;
    private String playerName;
    private List<String> deck;
}
