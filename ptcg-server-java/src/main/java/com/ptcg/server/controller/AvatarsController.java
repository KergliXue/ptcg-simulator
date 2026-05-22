package com.ptcg.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/avatars")
public class AvatarsController {

    @GetMapping("/list")
    public ResponseEntity<?> listAvatars() {
        return ResponseEntity.ok(Map.of(
                "avatars", List.of()
        ));
    }

    @GetMapping("/list/{userId}")
    public ResponseEntity<?> listAvatarsForUser(@PathVariable long userId) {
        return ResponseEntity.ok(Map.of(
                "avatars", List.of()
        ));
    }

    @PostMapping("/find")
    public ResponseEntity<?> findAvatar(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(Map.of(
                "avatar", null
        ));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addAvatar(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(Map.of(
                "avatar", Map.of("id", 1)
        ));
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteAvatar(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @PostMapping("/rename")
    public ResponseEntity<?> renameAvatar(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @PostMapping("/markAsDefault")
    public ResponseEntity<?> markAsDefault(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(Map.of("ok", true));
    }
}
