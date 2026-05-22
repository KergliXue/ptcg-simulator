package com.ptcg.server.model.state;

import com.ptcg.server.factory.CardFactory;
import com.ptcg.server.model.card.basic.Card;
import lombok.Data;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class CardList {

    private List<Card> cards = new ArrayList<>();
    private boolean isPublic = false;
    private boolean isSecret = false;

    public void add(Card card) {
        cards.add(card);
    }

    public boolean remove(Card card) {
        return cards.remove(card);
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    /**
     * Draw/pop the top 'count' cards from this list (e.g. from deck).
     */
    public List<Card> draw(int count) {
        List<Card> drawn = new ArrayList<>();
        int toDraw = Math.min(count, cards.size());
        for (int i = 0; i < toDraw; i++) {
            drawn.add(cards.remove(0));
        }
        return drawn;
    }

    public void moveTo(CardList destination) {
        moveTo(destination, cards.size());
    }

    public void moveTo(CardList destination, int count) {
        count = Math.min(count, cards.size());
        List<Card> moving = new ArrayList<>(cards.subList(0, count));
        cards.subList(0, count).clear();
        destination.cards.addAll(moving);
    }

    public void moveCardTo(Card card, CardList destination) {
        if (cards.remove(card)) {
            destination.cards.add(card);
        }
    }

    public void moveCardsTo(List<Card> cardsToMove, CardList destination) {
        for (Card card : cardsToMove) {
            if (cards.remove(card)) {
                destination.cards.add(card);
            }
        }
    }

    public void moveToTop(CardList destination) {
        moveToTop(destination, cards.size());
    }

    public void moveToTop(CardList destination, int count) {
        count = Math.min(count, cards.size());
        List<Card> moving = new ArrayList<>(cards.subList(0, count));
        cards.subList(0, count).clear();
        destination.cards.addAll(0, moving);
    }

    public void moveCardsToTop(List<Card> cardsToMove, CardList destination) {
        for (int i = cardsToMove.size() - 1; i >= 0; i--) {
            Card card = cardsToMove.get(i);
            if (cards.remove(card)) {
                destination.cards.add(0, card);
            }
        }
    }

    public void moveCardToTop(Card card, CardList destination) {
        if (cards.remove(card)) {
            destination.cards.add(0, card);
        }
    }

    public void applyOrder(int[] order) {
        if (cards.size() != order.length) {
            return;
        }
        List<Card> copy = new ArrayList<>(cards);
        for (int i = 0; i < order.length; i++) {
            cards.set(i, copy.get(order[i]));
        }
    }

    public static CardList fromList(List<String> cardKeys) {
        CardList cardList = new CardList();
        for (String key : cardKeys) {
            cardList.cards.add(CardFactory.createCard(key));
        }
        return cardList;
    }

    public List<Card> top(int count) {
        count = Math.min(count, cards.size());
        return new ArrayList<>(cards.subList(0, count));
    }

    public int size() {
        return cards.size();
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }
}
