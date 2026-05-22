package com.ptcg.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/replays")
public class ReplaysController {

    @GetMapping("/list/{page}")
    public ResponseEntity<?> listReplays(@PathVariable int page) {
        return ResponseEntity.ok(Map.of(
                "replays", List.of(),
                "totalCount", 0
        ));
    }

    @PostMapping("/list/{page}")
    public ResponseEntity<?> searchReplays(@PathVariable int page, @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(Map.of(
                "replays", List.of(),
                "totalCount", 0
        ));
    }

    @GetMapping("/match/{matchId}")
    public ResponseEntity<?> getMatch(@PathVariable long matchId) {
        return ResponseEntity.ok(Map.of(
                "replayData", ""
        ));
    }

    @GetMapping("/get/{replayId}")
    public ResponseEntity<?> getReplay(@PathVariable long replayId) {
        return ResponseEntity.ok(Map.of(
                "replayData", ""
        ));
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveReplay(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(Map.of(
                "ok", true,
                "replay", Map.of("id", body.getOrDefault("id", 1))
        ));
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteReplay(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @PostMapping("/rename")
    public ResponseEntity<?> renameReplay(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @PostMapping("/import")
    public ResponseEntity<?> importReplay(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(Map.of(
                "ok", true,
                "replay", Map.of("id", 1)
        ));
    }
}
