package com.ptcg.server.model.prompt;

import lombok.Getter;

@Getter
public class CoinFlipPrompt extends GamePrompt<Boolean> {

    public CoinFlipPrompt(int playerId, String message) {
        super(playerId, message);
    }

    @Override
    public String getType() {
        return "Coin flip";
    }
}
