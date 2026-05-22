package com.ptcg.server.controller;

import com.ptcg.server.engine.GameLogic;
import com.ptcg.server.factory.CardFactory;
import com.ptcg.server.model.action.AddPlayerAction;
import com.ptcg.server.model.card.basic.Card;
import com.ptcg.server.model.state.*;
import com.ptcg.server.serializer.StateSerializer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/game")
public class GameController {

    @GetMapping("/{gameId}/playerStats")
    public ResponseEntity<?> getPlayerStats(@PathVariable long gameId) {
        return ResponseEntity.ok(Map.of(
                "playerStats", List.of()
        ));
    }

    @GetMapping("/debug/serialize")
    public ResponseEntity<?> debugSerialize() {
        try {
            State state = new State();
            GameLogic logic = new GameLogic(null);
            logic.setState(state);

            // Simulate adding one player with a deck using BOTH registered keys and fullName keys
            List<String> deck = List.of(
                    "Base-GrassEnergy", "Base-GrassEnergy", "Base-GrassEnergy",
                    "Base-GrassEnergy", "Base-GrassEnergy", "Base-GrassEnergy",
                    "Base-GrassEnergy", "Base-GrassEnergy", "Base-GrassEnergy",
                    "Base-GrassEnergy",
                    "CSV2C-Ralts", "CSV2C-Ralts", "CSV2C-Ralts", "CSV2C-Ralts",
                    "CSV2C-Kirlia", "CSV2C-Kirlia", "CSV2C-Kirlia",
                    "CSV2C-GardevoirEx", "CSV2C-GardevoirEx",
                    "Base-NestBall", "Base-NestBall", "Base-NestBall", "Base-NestBall",
                    "Base-UltraBall", "Base-UltraBall", "Base-UltraBall", "Base-UltraBall",
                    "Base-SuperRod", "Base-SuperRod",
                    "Base-BuddyBuddyPoffin", "Base-BuddyBuddyPoffin", "Base-BuddyBuddyPoffin", "Base-BuddyBuddyPoffin",
                    "Base-PokemonSwitch", "Base-PokemonSwitch",
                    "CSV8C-Munkidori", "CSV8C-Munkidori",
                    // Remaining to reach 60
                    "Base-GrassEnergy", "Base-GrassEnergy", "Base-GrassEnergy",
                    "Base-GrassEnergy", "Base-GrassEnergy", "Base-GrassEnergy",
                    "Base-GrassEnergy", "Base-GrassEnergy", "Base-GrassEnergy",
                    "Base-GrassEnergy", "Base-GrassEnergy", "Base-GrassEnergy",
                    "Base-GrassEnergy", "Base-GrassEnergy", "Base-GrassEnergy",
                    "Base-GrassEnergy", "Base-GrassEnergy", "Base-GrassEnergy",
                    "Base-GrassEnergy", "Base-GrassEnergy", "Base-GrassEnergy",
                    "Base-GrassEnergy", "Base-GrassEnergy", "Base-GrassEnergy",
                    "Base-GrassEnergy", "Base-GrassEnergy", "Base-GrassEnergy"
            );

            // Create cards for the deck
            Player player = new Player(1, "TestPlayer");
            player.setUserId(1L);
            player.getDeck().setSecret(true);
            for (String key : deck) {
                try {
                    Card card = CardFactory.createCard(key);
                    state.getCardNames().add(card.getFullName());
                    card.setId(state.getCardNames().size() - 1);
                    player.getDeck().getCards().add(card);
                } catch (Exception e) {
                    state.getCardNames().add("ERROR-" + key + ":" + e.getMessage());
                }
            }
            state.getPlayers().add(player);

            StateSerializer serializer = new StateSerializer();
            String json = serializer.serialize(state);

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("cardNames", state.getCardNames());
            result.put("cardCount", state.getCardNames().size());
            result.put("jsonLength", json.length());
            result.put("stateJson", json);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("error", e.getMessage());
            error.put("stackTrace", Arrays.stream(e.getStackTrace())
                    .map(StackTraceElement::toString)
                    .limit(20)
                    .toList());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/debug/test-fullname")
    public ResponseEntity<?> debugTestFullName() {
        try {
            // Test creating cards with fullName format keys (as the frontend would send)
            List<String> fullNameKeys = List.of(
                    "Ralts CSV2C 053",
                    "Kirlia CSV2C 054",
                    "Gardevoir ex CSV2C 055",
                    "Nest Ball CSV2C 110",
                    "Ultra Ball CSV1C 112",
                    "Super Rod CSV1C 109",
                    "Buddy-Buddy Poffin CSV7C 177",
                    "Pokemon Switch CSV1C 113",
                    "Munkidori CSV8C 094",
                    "Grass Energy CSVSC GRA",
                    "Jet Energy CSV4C 129"
            );

            Map<String, Object> result = new LinkedHashMap<>();
            List<Map<String, Object>> cards = new ArrayList<>();
            for (String key : fullNameKeys) {
                Map<String, Object> info = new LinkedHashMap<>();
                info.put("key", key);
                try {
                    Card card = CardFactory.createCard(key);
                    info.put("ok", true);
                    info.put("fullName", card.getFullName());
                    info.put("name", card.getName());
                    info.put("set", card.getSet());
                    info.put("superType", card.getSuperType().name());
                } catch (Exception e) {
                    info.put("ok", false);
                    info.put("error", e.getMessage());
                }
                cards.add(info);
            }
            result.put("cards", cards);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
