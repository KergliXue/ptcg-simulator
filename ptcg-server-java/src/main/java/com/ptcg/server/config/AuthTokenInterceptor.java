package com.ptcg.server.config;

import com.ptcg.server.dto.ApiError;
import com.ptcg.server.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class AuthTokenInterceptor implements HandlerInterceptor {

    private final TokenService tokenService;

    public AuthTokenInterceptor(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        AuthToken authToken = handlerMethod.getMethodAnnotation(AuthToken.class);
        if (authToken == null) {
            return true;
        }

        String token = request.getHeader("Auth-Token");
        long userId = tokenService.validateToken(token);

        if (userId == 0) {
            sendError(response, ApiError.AUTH_TOKEN_INVALID);
            return false;
        }

        request.setAttribute("userId", userId);
        return true;
    }

    private void sendError(HttpServletResponse response, ApiError error) throws IOException {
        response.setStatus(403);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"" + error.toCode() + "\"}");
    }
}
