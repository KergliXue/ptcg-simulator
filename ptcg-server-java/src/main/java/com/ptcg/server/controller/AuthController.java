package com.ptcg.server.controller;

import com.ptcg.server.dto.*;
import com.ptcg.server.service.AuthException;
import com.ptcg.server.service.AuthService;
import com.ptcg.server.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/login")
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;

    public AuthController(AuthService authService, TokenService tokenService) {
        this.authService = authService;
        this.tokenService = tokenService;
    }

    @PostMapping("")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        LoginResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(@RequestHeader("Auth-Token") String token) {
        long userId = tokenService.validateToken(token);
        if (userId == 0) {
            throw new AuthException(ApiError.AUTH_TOKEN_INVALID);
        }
        LoginResponse response = authService.refreshToken(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @GetMapping("/info")
    public ResponseEntity<?> info() {
        return ResponseEntity.ok(Map.of(
                "ok", true,
                "config", authService.getServerConfig()
        ));
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<?> handleAuthException(AuthException e) {
        return ResponseEntity.badRequest()
                .body(Map.of("error", e.getError().toCode()));
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(org.springframework.web.bind.MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest()
                .body(Map.of("error", ApiError.VALIDATION_INVALID_PARAM.toCode()));
    }
}
