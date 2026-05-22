package com.ptcg.server.store;

import com.ptcg.server.model.state.State;
import com.ptcg.server.model.card.basic.Card;
import java.util.List;
import java.util.function.Consumer;

public interface GameStore {
    void prompt(State state, Object prompt, Consumer<List<Card>> callback);
}
