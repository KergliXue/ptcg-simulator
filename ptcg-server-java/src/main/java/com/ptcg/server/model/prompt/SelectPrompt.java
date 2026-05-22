package com.ptcg.server.model.prompt;

import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class SelectPrompt extends GamePrompt<Integer> {
    private List<Map<String, Object>> options;

    public SelectPrompt(int playerId, String message, List<Map<String, Object>> options) {
        super(playerId, message);
        this.options = options;
    }

    @Override
    public String getType() {
        return "Select";
    }
}
