package com.ptcg.server.model.prompt;

import lombok.Getter;

@Getter
public class ChooseAttackPrompt extends GamePrompt<String> {
    private boolean allowPass;

    public ChooseAttackPrompt(int playerId, String message, boolean allowPass) {
        super(playerId, message);
        this.allowPass = allowPass;
    }

    @Override
    public String getType() {
        return "Choose attack";
    }
}
