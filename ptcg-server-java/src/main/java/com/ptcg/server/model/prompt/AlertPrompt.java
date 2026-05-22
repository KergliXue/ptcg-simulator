package com.ptcg.server.model.prompt;

import lombok.Getter;

@Getter
public class AlertPrompt extends GamePrompt<Void> {

    public AlertPrompt(int playerId, String message) {
        super(playerId, message);
    }

    @Override
    public String getType() {
        return "Alert";
    }
}
