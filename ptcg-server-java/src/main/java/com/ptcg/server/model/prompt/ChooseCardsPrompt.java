package com.ptcg.server.model.prompt;

import com.ptcg.server.model.card.basic.Card;
import com.ptcg.server.model.state.CardList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
public class ChooseCardsPrompt extends GamePrompt<List<Card>> {
    private final CardList cards;
    private final Object filter;
    private final Options options;

    public ChooseCardsPrompt(int playerId, String message, CardList cards,
                              int min, int max) {
        super(playerId, message);
        this.cards = cards;
        this.filter = null;
        this.options = new Options(min, max);
    }

    public ChooseCardsPrompt(int playerId, String message, CardList cards,
                              Object filter, int min, int max) {
        super(playerId, message);
        this.cards = cards;
        this.filter = filter;
        this.options = new Options(min, max);
    }

    public ChooseCardsPrompt(int playerId, String message, CardList cards,
                              Object filter, Options options) {
        super(playerId, message);
        this.cards = cards;
        this.filter = filter;
        this.options = options;
    }

    @Override
    public String getType() {
        return "Choose cards";
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Card> decode(Object rawResult) {
        if (rawResult == null) return null;
        if (rawResult instanceof List<?> list) {
            if (list.isEmpty() || list.get(0) instanceof Card) {
                return (List<Card>) list;
            }
            List<Card> cardList = cards.getCards();
            return list.stream()
                    .map(i -> cardList.get(((Number) i).intValue()))
                    .toList();
        }
        return (List<Card>) rawResult;
    }

    @Data
    public static class Options {
        private int min;
        private int max;
        private boolean allowCancel = true;
        private List<Integer> blocked = new ArrayList<>();
        private boolean isSecret = false;
        private boolean differentTypes = false;
        private Integer maxPokemons = null;
        private Integer maxEnergies = null;
        private Integer maxTrainers = null;

        public Options() {}

        public Options(int min, int max) {
            this.min = min;
            this.max = max;
        }
    }
}
