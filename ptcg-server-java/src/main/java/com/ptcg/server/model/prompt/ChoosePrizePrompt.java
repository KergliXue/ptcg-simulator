package com.ptcg.server.model.prompt;

import com.ptcg.server.model.card.basic.Card;
import com.ptcg.server.model.state.CardList;
import lombok.Getter;

import java.util.List;

@Getter
public class ChoosePrizePrompt extends GamePrompt<List<Card>> {
    private CardList prizes;
    private int min;
    private int max;

    public ChoosePrizePrompt(int playerId, String message, CardList prizes, int min, int max) {
        super(playerId, message);
        this.prizes = prizes;
        this.min = min;
        this.max = max;
    }

    @Override
    public String getType() {
        return "Choose prize";
    }
}
