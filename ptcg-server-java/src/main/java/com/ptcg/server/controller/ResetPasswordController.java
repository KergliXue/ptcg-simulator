package com.ptcg.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/resetPassword")
public class ResetPasswordController {

    @PostMapping("/sendMail")
    public ResponseEntity<?> sendMail(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(Map.of("ok", true));
    }
}
