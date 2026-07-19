package com.history.config;

import com.history.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * WebSocket 握手期 JWT 校验拦截器
 * 安全修复 S6：原 /ws-game 端点无鉴权，任何人都能连。
 * SockJS 不支持自定义 header，token 通过 query string ?token=xxx 传递。
 * 校验失败拒绝握手（返回 401），成功则把 username 放入 attributes 供后续使用。
 */
@Component
@Slf4j
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {
        if (!(request instanceof ServletServerHttpRequest servletRequest)) {
            return false;
        }
        HttpServletRequest httpReq = servletRequest.getServletRequest();
        String token = httpReq.getParameter("token");

        if (token == null || token.isBlank()) {
            log.warn("WebSocket 握手拒绝：未提供 token, remote={}", httpReq.getRemoteAddr());
            response.setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
            return false;
        }

        if (!JwtUtil.validateToken(token)) {
            log.warn("WebSocket 握手拒绝：token 无效, remote={}", httpReq.getRemoteAddr());
            response.setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
            return false;
        }

        String username = JwtUtil.extractUsername(token);
        attributes.put("username", username);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
        // 无需后置处理
    }
}
