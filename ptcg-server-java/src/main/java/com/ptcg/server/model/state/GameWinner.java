package com.ptcg.server.model.state;

import com.fasterxml.jackson.annotation.JsonValue;

public enum GameWinner {
    NONE(-1),
    PLAYER_1(0),
    PLAYER_2(1),
    DRAW(3);

    private final int value;

    GameWinner(int value) {
        this.value = value;
    }

    @JsonValue
    public int getValue() {
        return value;
    }
}
