package com.ptcg.server.model.prompt;

import lombok.Data;

@Data
public abstract class GamePrompt<T> {
    private int id;
    private int playerId;
    private String message;
    private T result;

    protected GamePrompt(int playerId, String message) {
        this.playerId = playerId;
        this.message = message;
    }

    public abstract String getType();

    @SuppressWarnings("unchecked")
    public T decode(Object rawResult) {
        return (T) rawResult;
    }
}
