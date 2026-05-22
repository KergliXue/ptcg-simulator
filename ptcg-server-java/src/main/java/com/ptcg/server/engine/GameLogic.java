package com.ptcg.server.engine;

import com.ptcg.server.engine.effect_reducers.*;
import com.ptcg.server.engine.reducers.*;
import com.ptcg.server.model.action.*;
import com.ptcg.server.model.card.basic.Card;
import com.ptcg.server.model.effect.GameEffect;
import com.ptcg.server.model.prompt.GamePrompt;
import com.ptcg.server.model.state.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GameLogic {

    private static final Logger log = LoggerFactory.getLogger(GameLogic.class);

    private final GameStoreAdapter storeAdapter;
    private State state = new State();
    private final List<PromptItem> promptItems = new ArrayList<>();
    private final List<Runnable> waitItems = new ArrayList<>();
    private int logId = 0;

    public GameLogic(GameStoreAdapter storeAdapter) {
        this.storeAdapter = storeAdapter;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    // =========================================================================
    // DISPATCH
    // =========================================================================

    public State dispatch(GameAction action) {
        System.out.println("[dispatch] action=" + action.getType() + " promptItems=" + promptItems.size()
            + " statePrompts=" + state.getPrompts().size());
        if (action instanceof AbortGameAction) {
            state = AbortGameReducer.reduce(this, state, (AbortGameAction) action);
            onChangeState();
            return state;
        }

        if (action instanceof ResolvePromptAction rpa) {
            System.out.println("[dispatch] resolving prompt id=" + rpa.getPromptId());
            state = reducePrompt(state, rpa);
            if (promptItems.isEmpty()) {
                state = CheckEffectReducer.checkState(this, state);
            }
            onChangeState();
            return state;
        }

        if (hasUnresolvedPrompts()) {
            System.out.println("[dispatch] REJECTED: unresolved prompts pending");
            log.warn("Action {} rejected: prompts still pending", action.getType());
            return state;
        }

        System.out.println("[dispatch] calling reduce");
        state = reduce(state, action);
        System.out.println("[dispatch] reduce returned, phase=" + state.getPhase()
            + ", promptItems=" + promptItems.size());
        return state;
    }

    // =========================================================================
    // REDUCE
    // =========================================================================

    private State reduce(State state, GameAction action) {
        promptItems.clear();

        try {
            state = SetupReducer.reduce(this, state, action);
            state = PlayCardReducer.reduce(this, state, action);
            state = PlayerTurnReducer.reduce(this, state, action);

            resolveWaitItems();
            if (promptItems.isEmpty()) {
                state = CheckEffectReducer.checkState(this, state);
            }
        } catch (RuntimeException e) {
            promptItems.clear();
            throw e;
        }

        onChangeState();
        return state;
    }

    // =========================================================================
    // EFFECT REDUCTION
    // =========================================================================

    public State reduceEffect(State state, GameEffect effect) {
        state = propagateEffect(state, effect);

        if (effect.isPreventDefault()) {
            return state;
        }

        state = GamePhaseReducer.reduce(this, state, effect);
        state = PlayEnergyReducer.reduce(this, state, effect);
        state = PlayPokemonReducer.reduce(this, state, effect);
        state = PlayTrainerReducer.reduce(this, state, effect);
        state = RetreatReducer.reduce(this, state, effect);
        state = AttackReducer.reduce(this, state, effect);
        state = CheckEffectReducer.reduce(this, state, effect);

        return state;
    }

    private State propagateEffect(State state, GameEffect effect) {
        List<Card> allCards = new ArrayList<>();
        for (Player player : state.getPlayers()) {
            allCards.addAll(player.getStadium().getCards());
            allCards.addAll(player.getSupporter().getCards());
            allCards.addAll(player.getActive().getTrainers());
            allCards.addAll(player.getActive().getEnergies());
            allCards.addAll(player.getActive().getPokemons());
            for (PokemonSlot bench : player.getBench()) {
                allCards.addAll(bench.getTrainers());
                allCards.addAll(bench.getEnergies());
                allCards.addAll(bench.getPokemons());
            }
            for (CardList prize : player.getPrizes()) {
                allCards.addAll(prize.getCards());
            }
            allCards.addAll(player.getHand().getCards());
            allCards.addAll(player.getDeck().getCards());
            allCards.addAll(player.getDiscard().getCards());
        }
        allCards.sort((a, b) -> Integer.compare(
                a.getSuperType().ordinal(), b.getSuperType().ordinal()));
        for (Card card : allCards) {
            state = card.reduceEffect(this, state, effect);
        }
        return state;
    }

    // =========================================================================
    // PROMPTS
    // =========================================================================

    @SuppressWarnings("unchecked")
    public <T> State prompt(State state, GamePrompt<T> prompt, Consumer<T> then) {
        prompt.setId(generatePromptId(state));
        prompt.setResult(null);
        state.getPrompts().add(prompt);

        PromptItem item = new PromptItem();
        item.ids = List.of(prompt.getId());
        item.then = (Consumer<Object>) then;
        promptItems.add(item);

        return state;
    }

    public State waitPrompt(State state, Runnable callback) {
        waitItems.add(callback);
        return state;
    }

    private int generatePromptId(State state) {
        return state.getPrompts().stream()
                .mapToInt(GamePrompt::getId).max().orElse(0) + 1;
    }

    public boolean hasUnresolvedPrompts() {
        return promptItems.stream()
                .anyMatch(p -> p.ids.stream()
                        .anyMatch(id -> findPromptById(state, id) != null
                                && findPromptById(state, id).getResult() == null));
    }

    private GamePrompt<?> findPromptById(State state, int id) {
        return state.getPrompts().stream()
                .filter(p -> p.getId() == id).findFirst().orElse(null);
    }

    @SuppressWarnings("unchecked")
    private State reducePrompt(State state, ResolvePromptAction action) {
        GamePrompt<?> prompt = findPromptById(state, action.getPromptId());
        PromptItem promptItem = promptItems.stream()
                .filter(p -> p.ids.contains(action.getPromptId())).findFirst().orElse(null);

        if (prompt == null || promptItem == null) {
            throw new IllegalStateException("Invalid prompt resolution: " + action.getPromptId());
        }

        // Decode: convert raw client data (e.g., card indices) to proper types
        Object decoded = prompt.decode(action.getResult());
        ((GamePrompt) prompt).setResult(decoded);

        boolean allResolved = promptItem.ids.stream()
                .map(id -> findPromptById(state, id))
                .allMatch(p -> p != null && p.getResult() != null);

        if (allResolved) {
            promptItems.remove(promptItem);
            promptItem.then.accept(promptItem.ids.size() == 1
                    ? prompt.getResult()
                    : promptItem.ids.stream()
                            .map(id -> findPromptById(state, id))
                            .map(GamePrompt::getResult).toList());
        }

        resolveWaitItems();
        return state;
    }

    private void resolveWaitItems() {
        while (promptItems.isEmpty() && !waitItems.isEmpty()) {
            Runnable waitItem = waitItems.remove(waitItems.size() - 1);
            waitItem.run();
        }
    }

    // =========================================================================
    // STATE CHANGE & LOGGING
    // =========================================================================

    private void onChangeState() {
        if (storeAdapter != null) {
            storeAdapter.onStateChange(state);
        }
    }

    public void log(String message, String params) {
        StateLog logEntry = new StateLog(++logId, message, params, 0);
        state.getLogs().add(logEntry);
    }

    // =========================================================================
    // INNER CLASSES
    // =========================================================================

    private static class PromptItem {
        List<Integer> ids;
        Consumer<Object> then;
    }

    public interface GameStoreAdapter {
        void onStateChange(State state);
    }
}
