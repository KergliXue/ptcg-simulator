package com.ptcg.server.service;

import cn.hutool.crypto.digest.HMac;
import cn.hutool.crypto.digest.HmacAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    private final String secret;
    private final long tokenExpire;

    public TokenService(
            @Value("${app.secret}") String secret,
            @Value("${app.token-expire}") long tokenExpire) {
        this.secret = secret;
        this.tokenExpire = tokenExpire;
    }

    public String generateToken(long userId) {
        long expire = System.currentTimeMillis() / 1000 + tokenExpire;
        return buildToken(userId, expire);
    }

    public long validateToken(String token) {
        if (token == null || token.isEmpty()) {
            return 0;
        }
        try {
            String[] parts = token.split(",");
            if (parts.length != 3) {
                return 0;
            }
            long userId = Long.parseLong(parts[0]);
            long expire = Long.parseLong(parts[1]);

            if (expire < System.currentTimeMillis() / 1000) {
                return 0;
            }

            if (!token.equals(buildToken(userId, expire))) {
                return 0;
            }
            return userId;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String buildToken(long userId, long expire) {
        HMac hMac = new HMac(HmacAlgorithm.HmacSHA256, secret.getBytes());
        String hash = hMac.digestHex(secret + userId + expire);
        return userId + "," + expire + "," + hash;
    }
}
