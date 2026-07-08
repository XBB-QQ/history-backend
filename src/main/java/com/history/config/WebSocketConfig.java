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
        registry.addEndpoint("/ws-game")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
