package com.ptcg.server.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptcg.server.config.AuthToken;
import com.ptcg.server.entity.Deck;
import com.ptcg.server.mapper.DeckMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/decks")
public class DecksController {

    private final DeckMapper deckMapper;
    private final ObjectMapper objectMapper;

    public DecksController(DeckMapper deckMapper, ObjectMapper objectMapper) {
        this.deckMapper = deckMapper;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/list")
    @AuthToken
    public ResponseEntity<?> listDecks(HttpServletRequest request) {
        long userId = getUserId(request);
        List<Deck> decks = deckMapper.selectList(new LambdaQueryWrapper<Deck>()
                .eq(Deck::getUserId, userId));
        List<Map<String, Object>> result = decks.stream().map(deck -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", deck.getId());
            m.put("name", deck.getName());
            m.put("isValid", deck.getIsValid() != null ? deck.getIsValid() : false);
            m.put("formatNames", parseJsonArray(deck.getFormatNames()));
            m.put("cardTypes", parseJsonArray(deck.getCardTypes()));
            return m;
        }).toList();
        return ResponseEntity.ok(Map.of("ok", true, "decks", result));
    }

    @GetMapping("/get/{id}")
    @AuthToken
    public ResponseEntity<?> getDeck(@PathVariable long id, HttpServletRequest request) {
        long userId = getUserId(request);
        Deck deck = deckMapper.selectOne(new LambdaQueryWrapper<Deck>()
                .eq(Deck::getId, id)
                .eq(Deck::getUserId, userId));
        if (deck == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "ERROR_DECK_INVALID"));
        }
        return ResponseEntity.ok(Map.of("ok", true, "deck", buildDeckMap(deck)));
    }

    @PostMapping("/save")
    @AuthToken
    public ResponseEntity<?> saveDeck(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        long userId = getUserId(request);
        String name = ((String) body.get("name")).trim();
        @SuppressWarnings("unchecked")
        List<String> cards = (List<String>) body.get("cards");

        Deck deck;
        if (body.get("id") != null) {
            long deckId = ((Number) body.get("id")).longValue();
            deck = deckMapper.selectOne(new LambdaQueryWrapper<Deck>()
                    .eq(Deck::getId, deckId)
                    .eq(Deck::getUserId, userId));
            if (deck == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "ERROR_DECK_INVALID"));
            }
        } else {
            deck = new Deck();
            deck.setUserId(userId);
        }

        deck.setName(name);
        try {
            deck.setCards(objectMapper.writeValueAsString(cards));
        } catch (JsonProcessingException e) {
            deck.setCards("[]");
        }
        deck.setIsValid(cards != null && cards.size() == 60);
        deck.setFormatNames("[]");
        deck.setCardTypes("[]");

        if (deck.getId() != null) {
            deckMapper.updateById(deck);
        } else {
            deckMapper.insert(deck);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", deck.getId());
        result.put("name", deck.getName());
        result.put("cards", cards);
        return ResponseEntity.ok(Map.of("ok", true, "deck", result));
    }

    @PostMapping("/delete")
    @AuthToken
    public ResponseEntity<?> deleteDeck(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        long userId = getUserId(request);
        long deckId = ((Number) body.get("id")).longValue();
        Deck deck = deckMapper.selectOne(new LambdaQueryWrapper<Deck>()
                .eq(Deck::getId, deckId)
                .eq(Deck::getUserId, userId));
        if (deck == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "ERROR_DECK_INVALID"));
        }
        deckMapper.deleteById(deckId);
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @PostMapping("/rename")
    @AuthToken
    public ResponseEntity<?> renameDeck(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        long userId = getUserId(request);
        long deckId = ((Number) body.get("id")).longValue();
        String name = ((String) body.get("name")).trim();
        Deck deck = deckMapper.selectOne(new LambdaQueryWrapper<Deck>()
                .eq(Deck::getId, deckId)
                .eq(Deck::getUserId, userId));
        if (deck == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "ERROR_DECK_INVALID"));
        }
        deck.setName(name);
        deckMapper.updateById(deck);
        return ResponseEntity.ok(Map.of("ok", true, "deck", Map.of("id", deck.getId(), "name", deck.getName())));
    }

    @PostMapping("/duplicate")
    @AuthToken
    public ResponseEntity<?> duplicateDeck(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        long userId = getUserId(request);
        long deckId = ((Number) body.get("id")).longValue();
        String newName = ((String) body.get("name")).trim();
        Deck original = deckMapper.selectOne(new LambdaQueryWrapper<Deck>()
                .eq(Deck::getId, deckId)
                .eq(Deck::getUserId, userId));
        if (original == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "ERROR_DECK_INVALID"));
        }
        Deck duplicate = new Deck();
        duplicate.setUserId(userId);
        duplicate.setName(newName);
        duplicate.setCards(original.getCards());
        duplicate.setIsValid(original.getIsValid());
        duplicate.setFormatNames(original.getFormatNames());
        duplicate.setCardTypes(original.getCardTypes());
        deckMapper.insert(duplicate);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", duplicate.getId());
        result.put("name", duplicate.getName());
        result.put("cards", parseJsonArray(original.getCards()));
        return ResponseEntity.ok(Map.of("ok", true, "deck", result));
    }

    private long getUserId(HttpServletRequest request) {
        return ((Number) request.getAttribute("userId")).longValue();
    }

    private Map<String, Object> buildDeckMap(Deck deck) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", deck.getId());
        m.put("name", deck.getName());
        m.put("isValid", deck.getIsValid() != null ? deck.getIsValid() : false);
        m.put("formatNames", parseJsonArray(deck.getFormatNames()));
        m.put("cardTypes", parseJsonArray(deck.getCardTypes()));
        m.put("cards", parseJsonArray(deck.getCards()));
        return m;
    }

    private List<?> parseJsonArray(String json) {
        if (json == null || json.isEmpty() || json.equals("[]")) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, List.class);
        } catch (Exception e) {
            return List.of();
        }
    }
}
