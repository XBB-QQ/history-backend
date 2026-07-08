package com.history.game.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 房间聊天消息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {
    private String playerId;
    private String playerName;
    private String content;
    private LocalDateTime timestamp;
}
