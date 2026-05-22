package com.ptcg.server.engine.reducers;

import com.ptcg.server.engine.GameLogic;
import com.ptcg.server.engine.effect_reducers.CheckEffectReducer;
import com.ptcg.server.model.action.AbortGameAction;
import com.ptcg.server.model.state.GamePhase;
import com.ptcg.server.model.state.GameWinner;
import com.ptcg.server.model.state.State;

public class AbortGameReducer {

    public static State reduce(GameLogic logic, State state, AbortGameAction action) {
        if (state.getPhase() == GamePhase.FINISHED) {
            return state;
        }

        // The player who calls abort loses; the other player wins
        int firstPlayerId = state.getPlayers().get(0).getId();
        GameWinner winner = (firstPlayerId == action.getPlayerId())
                ? GameWinner.PLAYER_2 : GameWinner.PLAYER_1;

        return CheckEffectReducer.endGame(logic, state, winner);
    }
}
