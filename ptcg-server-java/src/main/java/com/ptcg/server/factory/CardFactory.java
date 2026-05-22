package com.ptcg.server.factory;

import com.ptcg.server.model.card.basic.Card;
import org.springframework.stereotype.Component;

@Component
public class CardFactory {

    private static CardRegistry registry;

    public CardFactory(CardRegistry registry) {
        CardFactory.registry = registry;
    }

    public static Card createCard(String cardKey) {
        return registry.createCard(cardKey);
    }

    public static boolean hasCard(String cardKey) {
        return registry.hasCard(cardKey);
    }

    public static int cardCount() {
        return registry.size();
    }
}
