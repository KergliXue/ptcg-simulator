package com.ptcg.server.service;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ptcg.server.dto.*;
import com.ptcg.server.entity.User;
import com.ptcg.server.mapper.UserMapper;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserMapper userMapper;
    private final TokenService tokenService;

    public AuthService(UserMapper userMapper, TokenService tokenService) {
        this.userMapper = userMapper;
        this.tokenService = tokenService;
    }

    public LoginResponse register(RegisterRequest request) {
        if (userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getName, request.getName())) > 0) {
            throw new AuthException(ApiError.REGISTER_NAME_EXISTS);
        }
        if (userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, request.getEmail())) > 0) {
            throw new AuthException(ApiError.REGISTER_EMAIL_EXISTS);
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(BCrypt.hashpw(request.getPassword()));
        user.setRegistered(System.currentTimeMillis());
        userMapper.insert(user);

        String token = tokenService.generateToken(user.getId());
        return new LoginResponse(true, token, getServerConfig());
    }

    public LoginResponse login(LoginRequest request) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getName, request.getName()));

        if (user == null || !BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new AuthException(ApiError.LOGIN_INVALID);
        }

        user.setLastSeen(System.currentTimeMillis());
        userMapper.updateById(user);

        String token = tokenService.generateToken(user.getId());
        return new LoginResponse(true, token, getServerConfig());
    }

    public LoginResponse refreshToken(long userId) {
        String token = tokenService.generateToken(userId);
        return new LoginResponse(true, token, getServerConfig());
    }

    public long validateToken(String token) {
        return tokenService.validateToken(token);
    }

    public ServerConfig getServerConfig() {
        return new ServerConfig(
                4,           // apiVersion
                50,          // defaultPageSize
                "/v1/scans",
                "/avatars/{name}",
                256 * 1024,  // avatarFileSize
                64,          // avatarMinSize
                512,         // avatarMaxSize
                512 * 1024   // replayFileSize
        );
    }
}
