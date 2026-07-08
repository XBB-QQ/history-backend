package com.history.game.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 房间事件 — 通过 STOMP 广播给房间所有订阅者
 *
 * type 取值：
 *   - player_join    玩家加入
 *   - player_leave   玩家离开
 *   - chat           聊天消息
 *   - character_pick 角色选择
 *   - clue_collect   线索收集
 *   - phase_change   阶段切换
 *   - reveal         真相揭示
 *   - error          错误
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomEvent {
    private String type;
    private String roomId;
    private String playerId;
    private String playerName;
    private Map<String, Object> payload;
    private LocalDateTime timestamp;
}
