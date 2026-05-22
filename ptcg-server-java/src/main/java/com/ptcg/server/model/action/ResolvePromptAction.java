package com.ptcg.server.model.action;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResolvePromptAction implements GameAction {
    private final String type = "RESOLVE_PROMPT_ACTION";
    private int promptId;
    private Object result;
}
