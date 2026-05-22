package com.ptcg.server.model.state;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StateLog {
    private int id;
    private String message;
    private String params;
    private int client;
}
