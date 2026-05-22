package com.ptcg.server.model.state;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ptcg.server.model.prompt.GamePrompt;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class State {

    private List<Player> players = new ArrayList<>();
    private GamePhase phase = GamePhase.WAITING_FOR_PLAYERS;

    @JsonProperty("activePlayer")
    private int activePlayerIndex = 0;
    private int turn = 0;

    private GameWinner winner = GameWinner.NONE;
    private List<StateLog> logs = new ArrayList<>();
    private List<GamePrompt<?>> prompts = new ArrayList<>();
    private List<String> cardNames = new ArrayList<>();
    private GameRules rules = new GameRules();

    @JsonIgnore
    public Player getActivePlayer() {
        if (players.isEmpty()) return null;
        return players.get(activePlayerIndex);
    }

    @JsonIgnore
    public Player getOpponentPlayer() {
        if (players.size() < 2) return null;
        return players.get(activePlayerIndex == 0 ? 1 : 0);
    }
}
