package com.ptcg.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/ranking")
public class RankingController {

    @GetMapping("/list/{page}")
    public ResponseEntity<?> getRanking(@PathVariable int page) {
        return ResponseEntity.ok(Map.of(
                "ranking", List.of(),
                "totalCount", 0
        ));
    }

    @PostMapping("/list/{page}")
    public ResponseEntity<?> searchRanking(@PathVariable int page, @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(Map.of(
                "ranking", List.of(),
                "totalCount", 0
        ));
    }
}
