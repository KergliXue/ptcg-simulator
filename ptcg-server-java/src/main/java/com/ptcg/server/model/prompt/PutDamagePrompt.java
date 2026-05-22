package com.ptcg.server.model.prompt;

import com.ptcg.server.model.state.CardList;
import lombok.Getter;

@Getter
public class PutDamagePrompt extends GamePrompt<int[]> {
    private CardList targets;
    private int totalDamage;

    public PutDamagePrompt(int playerId, String message, CardList targets, int totalDamage) {
        super(playerId, message);
        this.targets = targets;
        this.totalDamage = totalDamage;
    }

    @Override
    public String getType() {
        return "Put damage";
    }
}
