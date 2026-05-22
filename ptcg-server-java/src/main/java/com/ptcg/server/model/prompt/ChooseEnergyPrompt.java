package com.ptcg.server.model.prompt;

import com.ptcg.server.model.card.basic.Card;
import com.ptcg.server.model.state.CardList;
import lombok.Getter;

@Getter
public class ChooseEnergyPrompt extends GamePrompt<Card> {
    private CardList cards;
    private int min;
    private int max;
    private Object filter;

    public ChooseEnergyPrompt(int playerId, String message, CardList cards,
                              Object filter, int min, int max) {
        super(playerId, message);
        this.cards = cards;
        this.filter = filter;
        this.min = min;
        this.max = max;
    }

    @Override
    public String getType() {
        return "Choose energy";
    }
}
