package com.ptcg.server.controller;

import com.ptcg.server.config.AuthToken;
import com.ptcg.server.entity.User;
import com.ptcg.server.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    private final UserMapper userMapper;

    public ProfileController(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @GetMapping("/me")
    @AuthToken
    public ResponseEntity<?> getMe(HttpServletRequest request) {
        long userId = getUserId(request);
        User user = userMapper.selectById(userId);
        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "ERROR_PROFILE_INVALID"));
        }
        return ResponseEntity.ok(Map.of("ok", true, "user", buildUserMap(user)));
    }

    @GetMapping("/get/{userId}")
    @AuthToken
    public ResponseEntity<?> getUser(@PathVariable long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "ERROR_PROFILE_INVALID"));
        }
        return ResponseEntity.ok(Map.of("ok", true, "user", buildUserMap(user)));
    }

    @GetMapping("/matchHistory/{userId}/{page}")
    @AuthToken
    public ResponseEntity<?> getMatchHistory(@PathVariable long userId, @PathVariable int page) {
        return ResponseEntity.ok(Map.of(
                "ok", true,
                "matches", List.of(),
                "users", List.of(),
                "total", 0
        ));
    }

    @PostMapping("/changePassword")
    @AuthToken
    public ResponseEntity<?> changePassword(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @PostMapping("/changeEmail")
    @AuthToken
    public ResponseEntity<?> changeEmail(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(Map.of("ok", true));
    }

    private long getUserId(HttpServletRequest request) {
        return ((Number) request.getAttribute("userId")).longValue();
    }

    private Map<String, Object> buildUserMap(User user) {


        Map<String, Object> m = new LinkedHashMap<>();
        m.put("userId", user.getId());
        m.put("name", user.getName());
        m.put("email", user.getEmail() != null ? user.getEmail() : "");
        m.put("registered", user.getRegistered());
        m.put("lastSeen", user.getLastSeen());
        m.put("ranking", user.getRanking());
        m.put("rank", 0);
        m.put("lastRankingChange", user.getLastRankingChange());
        m.put("avatarFile", user.getAvatarFile() != null ? user.getAvatarFile() : "");
        m.put("connected", true);
        return m;
    }
}
