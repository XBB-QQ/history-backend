package com.history.game.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 联机剧本杀玩家
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Player {
    /** 玩家唯一 ID（前端生成的 UUID） */
    private String playerId;
    /** 显示名 */
    private String name;
    /** 是否房主 */
    private boolean isHost;
    /** 加入时间 */
    private LocalDateTime joinedAt;
}
