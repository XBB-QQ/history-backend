package com.history.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket STOMP 配置
 * @see ITERATIONS.md #101 联机剧本杀
 *
 * 端点 /ws-game（SockJS 兼容）
 * 应用 destination 前缀 /app/game/{roomId}/{action}
 * 广播 topic 前缀 /topic/game/{roomId}/{type}
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtHandshakeInterceptor jwtHandshakeInterceptor;

    public WebSocketConfig(JwtHandshakeInterceptor jwtHandshakeInterceptor) {
        this.jwtHandshakeInterceptor = jwtHandshakeInterceptor;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 服务端广播通道：订阅 /topic/game/** 即可收到房间消息
        registry.enableSimpleBroker("/topic");
        // 客户端发送消息前缀：发送到 /app/game/** 由 Controller 处理
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // STOMP 连接端点，SockJS 兼容（前端 sockjs-client 可连接）
        // L2 修复：从 FRONTEND_ORIGIN 环境变量读取允许的源，未配置时回退到 *（仅开发环境）
        String allowedOrigin = System.getenv("FRONTEND_ORIGIN");
        String[] origins = (allowedOrigin != null && !allowedOrigin.isBlank())
                ? allowedOrigin.split("\\s*,\\s*")
                : new String[]{"*"};
        // 安全修复 S6：握手期校验 JWT，未登录或 token 无效拒绝连接
        registry.addEndpoint("/ws-game")
                .addInterceptors(jwtHandshakeInterceptor)
                .setAllowedOriginPatterns(origins)
                .withSockJS();
    }
}
