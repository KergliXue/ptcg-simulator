package com.ptcg.server.model.action;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChangeAvatarAction implements GameAction {
    private final String type = "CHANGE_AVATAR";
    private int playerId;
    private String avatarName;
}
