package com.ptcg.server.model.action;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AppendLogAction implements GameAction {
    private final String type = "APPEND_LOG";
    private int playerId;
    private String logType;
    private Map<String, Object> params;
}
