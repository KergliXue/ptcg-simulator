package com.ptcg.server.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]{3,32}$")
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "^[^\\s]{5,32}$")
    private String password;

    private String serverPassword;
}
