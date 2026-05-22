package com.ptcg.server.model.prompt;

import lombok.Getter;

@Getter
public class ShuffleDeckPrompt extends GamePrompt<int[]> {

    public ShuffleDeckPrompt(int playerId) {
        super(playerId, "Shuffle your deck");
    }

    @Override
    public String getType() {
        return "Shuffle deck";
    }
}
