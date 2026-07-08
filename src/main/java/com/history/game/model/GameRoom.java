package com.history.game.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 联机剧本杀房间（内存状态，非持久化）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameRoom {
    /** 房间号（6位字母数字） */
    private String roomId;
    /** 剧本 ID（如 shang-mystery） */
    private String scriptId;
    /** 房主名 */
    private String hostName;
    /** 已加入玩家 */
    @Builder.Default
    private List<Player> players = new ArrayList<>();
    /** 当前阶段 */
    @Builder.Default
    private GamePhase phase = GamePhase.WAITING;
    /** 玩家 → 角色绑定（playerId → characterId） */
    @Builder.Default
    private Map<String, String> characterAssignments = new HashMap<>();
    /** 全房已收集线索文本（去重） */
    @Builder.Default
    private Set<String> collectedClues = new LinkedHashSet<>();
    /** 聊天历史 */
    @Builder.Default
    private List<ChatMessage> chatHistory = new ArrayList<>();
    /** 创建时间 */
    private LocalDateTime createdAt;
    /** 最近活动时间 */
    private LocalDateTime updatedAt;
}
