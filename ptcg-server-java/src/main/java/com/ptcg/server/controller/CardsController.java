package com.ptcg.server.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptcg.server.entity.CardEntity;
import com.ptcg.server.mapper.CardMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/cards")
public class CardsController {

    private final CardMapper cardMapper;
    private final ObjectMapper objectMapper;

    /** Cached cards info (as in TypeScript CardManager). */
    private Map<String, Object> cachedCardsInfo;

    public CardsController(CardMapper cardMapper, ObjectMapper objectMapper) {
        this.cardMapper = cardMapper;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/info")
    public ResponseEntity<?> getCardsInfo() {
        if (cachedCardsInfo == null) {
            long total = cardMapper.selectCount(null);
            Map<String, Object> cardsInfo = new LinkedHashMap<>();
            cardsInfo.put("cardsTotal", total);
            cardsInfo.put("formats", List.of());
            cardsInfo.put("hash", "");
            cachedCardsInfo = cardsInfo;
        }
        return ResponseEntity.ok(Map.of(
                "ok", true,
                "cardsInfo", cachedCardsInfo
        ));
    }

    @GetMapping("/get/{page}")
    public ResponseEntity<?> getCards(@PathVariable int page) {
        int pageSize = 50;
        List<CardEntity> entities = cardMapper.selectList(
                new LambdaQueryWrapper<CardEntity>()
                        .last("LIMIT " + pageSize + " OFFSET " + (page * pageSize)));

        List<Map<String, Object>> cards = new ArrayList<>();
        for (CardEntity entity : entities) {
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> cardData = objectMapper.readValue(entity.getCardData(), Map.class);
                cardData.put("arts", parseArts(entity.getArts()));
                // Ensure setNumber is present - parse from fullName if missing
                if (!cardData.containsKey("setNumber")) {
                    cardData.put("setNumber", parseSetNumber(entity.getFullName()));
                }
                cards.add(cardData);
            } catch (Exception e) {
                System.err.println("Failed to parse card data: " + entity.getCardKey() + " - " + e.getMessage());
            }
        }

        return ResponseEntity.ok(Map.of("ok", true, "cards", cards));
    }

    private List<Map<String, Object>> parseArts(String artsJson) {
        if (artsJson == null || artsJson.isEmpty() || artsJson.equals("[]")) {
            return List.of();
        }
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> arts = objectMapper.readValue(artsJson, List.class);
            return arts;
        } catch (Exception e) {
            return List.of();
        }
    }

    private String parseSetNumber(String fullName) {
        // fullName format: "CardName SetCode SetNumber" e.g., "Jet Energy CSV4C 129"
        if (fullName == null || fullName.isEmpty()) {
            return "";
        }
        String[] parts = fullName.split("\\s+");
        if (parts.length >= 2) {
            return parts[parts.length - 1];
        }
        return "";
    }
}
