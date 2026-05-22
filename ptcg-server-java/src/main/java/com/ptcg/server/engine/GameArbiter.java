package com.ptcg.server.engine;

import com.ptcg.server.model.action.ResolvePromptAction;
import com.ptcg.server.model.prompt.*;
import com.ptcg.server.model.state.*;

import java.util.Random;

public class GameArbiter {

    private static final Random random = new Random();

    /** Try to auto-resolve a prompt. Returns a ResolvePromptAction if resolved, null if user input needed. */
    public static ResolvePromptAction resolvePrompt(State state, GamePrompt<?> prompt) {
        if (prompt.getResult() != null) {
            return null; // Already resolved
        }

        if (prompt instanceof ShuffleDeckPrompt) {
            Player p = state.getPlayers().stream()
                    .filter(x -> x.getId() == prompt.getPlayerId())
                    .findFirst().orElse(null);
            if (p == null) {
                throw new IllegalStateException("Player not found with ID: " + prompt.getPlayerId());
            }
            int[] order = generateShuffle(p.getDeck());
            return new ResolvePromptAction(prompt.getId(), order);
        }

        if (prompt instanceof CoinFlipPrompt) {
            boolean result = random.nextBoolean();
            return new ResolvePromptAction(prompt.getId(), result);
        }

        // Other prompts need user interaction — don't auto-resolve
        return null;
    }

    private static int[] generateShuffle(CardList deck) {
        int len = deck.getCards().size();
        int[] order = new int[len];
        for (int i = 0; i < len; i++) {
            order[i] = i;
        }
        for (int i = len - 1; i > 0; i--) {
            int pos = random.nextInt(i + 1);
            int temp = order[i];
            order[i] = order[pos];
            order[pos] = temp;
        }
        return order;
    }
}
