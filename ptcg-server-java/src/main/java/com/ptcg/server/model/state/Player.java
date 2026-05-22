package com.ptcg.server.model.state;

import com.ptcg.server.model.action.CardTarget;
import com.ptcg.server.model.action.PlayerType;
import com.ptcg.server.model.action.SlotType;
import com.ptcg.server.model.card.basic.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

@Data
public class Player {

    private int id;
    private long userId;
    private String name = "";

    private CardList deck = new CardList();
    private CardList hand = new CardList();
    private CardList discard = new CardList();
    private CardList stadium = new CardList();
    private CardList supporter = new CardList();

    private List<CardList> prizes = new ArrayList<>();

    private PokemonSlot active = new PokemonSlot();
    private List<PokemonSlot> bench = new ArrayList<>();

    private int retreatedTurn = 0;
    private int energyPlayedTurn = 0;
    private int stadiumPlayedTurn = 0;
    private int stadiumUsedTurn = 0;

    private PlayerMarker marker = new PlayerMarker();
    private String avatarName = "";

    public Player(int id, String name) {
        this.id = id;
        this.name = name;

        for (int i = 0; i < 6; i++) {
            prizes.add(new CardList());
        }

        for (int i = 0; i < 5; i++) {
            bench.add(new PokemonSlot());
        }
    }

    public Player() {
        this(0, "");
    }

    public int getPrizeLeft() {
        return prizes.stream().mapToInt(p -> p.getCards().size()).sum();
    }

    public List<Card> drawCards(int count) {
        List<Card> drawn = deck.draw(count);
        for (Card card : drawn) {
            hand.add(card);
        }
        return drawn;
    }

    public void putCardOnTopOfDeck(Card card) {
        if (hand.remove(card)) {
            deck.getCards().add(0, card);
        }
    }

    public void shuffleDeck() {
        deck.shuffle();
    }

    public void discardFromHand(Card card) {
        if (hand.remove(card)) {
            discard.add(card);
        }
    }

    public void discardFromHand(List<Card> cards) {
        for (Card card : cards) {
            discardFromHand(card);
        }
    }

    public void discardSlot(PokemonSlot slot) {
        discard.getCards().addAll(slot.getPokemons());
        discard.getCards().addAll(slot.getEnergies());
        discard.getCards().addAll(slot.getTrainers());
        slot.getPokemons().clear();
        slot.getEnergies().clear();
        slot.getTrainers().clear();
        slot.clearEffects();
    }

    public boolean retrieveFromDiscardToHand(Card card) {
        if (discard.remove(card)) {
            hand.add(card);
            return true;
        }
        return false;
    }

    public boolean retrieveFromDiscardToDeck(Card card) {
        if (discard.remove(card)) {
            deck.add(card);
            deck.shuffle();
            return true;
        }
        return false;
    }

    public boolean playBasicPokemonToBench(PokemonCard card, int benchIndex) {
        if (benchIndex < 0 || benchIndex >= bench.size()) {
            return false;
        }

        PokemonSlot slot = bench.get(benchIndex);
        if (!slot.getPokemons().isEmpty()) {
            return false;
        }

        if (hand.remove(card)) {
            slot.getPokemons().add(card);
            return true;
        }
        return false;
    }

    public boolean attachEnergyFromHand(EnergyCard card, PokemonSlot slot) {
        if (hand.remove(card)) {
            slot.getEnergies().add(card);
            return true;
        }
        return false;
    }

    public void switchPokemon(PokemonSlot target) {
        int benchIndex = bench.indexOf(target);
        if (benchIndex != -1) {
            active.clearEffects();
            PokemonSlot temp = active;
            active = bench.get(benchIndex);
            bench.set(benchIndex, temp);
        }
    }

    public void forEachPokemon(PlayerType playerType,
                                BiConsumer<PokemonSlot, CardTarget> handler) {
        PokemonCard pokemonCard = active.getPokemonCard();
        if (pokemonCard != null) {
            CardTarget target = new CardTarget(playerType, SlotType.ACTIVE, 0);
            handler.accept(active, target);
        }
        for (int i = 0; i < bench.size(); i++) {
            pokemonCard = bench.get(i).getPokemonCard();
            if (pokemonCard != null) {
                CardTarget target = new CardTarget(playerType, SlotType.BENCH, i);
                handler.accept(bench.get(i), target);
            }
        }
    }
}
