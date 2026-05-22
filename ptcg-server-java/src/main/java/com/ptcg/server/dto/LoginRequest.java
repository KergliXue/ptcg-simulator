package com.ptcg.server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]{3,32}$")
    private String name;

    @NotBlank
    private String password;
}
