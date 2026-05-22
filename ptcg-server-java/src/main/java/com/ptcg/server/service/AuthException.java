package com.ptcg.server.service;

import com.ptcg.server.dto.ApiError;

public class AuthException extends RuntimeException {
    private final ApiError error;

    public AuthException(ApiError error) {
        super(error.toCode());
        this.error = error;
    }

    public ApiError getError() {
        return error;
    }
}
