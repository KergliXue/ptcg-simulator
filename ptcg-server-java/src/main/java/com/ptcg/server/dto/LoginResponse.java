package com.ptcg.server.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {
    private boolean ok;
    private String token;
    private ServerConfig config;
}
