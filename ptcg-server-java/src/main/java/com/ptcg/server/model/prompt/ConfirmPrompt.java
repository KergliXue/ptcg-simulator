package com.ptcg.server.model.prompt;

import lombok.Getter;

@Getter
public class ConfirmPrompt extends GamePrompt<Boolean> {

    public ConfirmPrompt(int playerId, String message) {
        super(playerId, message);
    }

    @Override
    public String getType() {
        return "Confirm";
    }
}
