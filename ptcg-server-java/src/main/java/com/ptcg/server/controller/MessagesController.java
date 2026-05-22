package com.ptcg.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/messages")
public class MessagesController {

    @GetMapping("/list")
    public ResponseEntity<?> getConversations() {
        return ResponseEntity.ok(Map.of(
                "ok", true,
                "conversations", List.of(),
                "users", List.of()
        ));
    }

    @GetMapping("/get/{userId}")
    public ResponseEntity<?> getMessages(@PathVariable long userId) {
        return ResponseEntity.ok(Map.of(
                "ok", true,
                "messages", List.of(),
                "users", List.of()
        ));
    }

    @PostMapping("/deleteMessages")
    public ResponseEntity<?> deleteMessages(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(Map.of("ok", true));
    }
}
