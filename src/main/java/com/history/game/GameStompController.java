package com.history.game;

import com.history.game.dto.GameMessage;
import com.history.game.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 联机剧本杀 STOMP 消息处理
 *
 * 客户端发送：/app/game/{roomId}/{action}
 * 服务端广播：/topic/game/{roomId}/events
 *
 * 所有事件统一以 {@link RoomEvent} 包装广播，前端按 type 字段分发处理。
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class GameStompController {

    private final GameRoomService roomService;
    private final SimpMessagingTemplate messaging;

    private static final String TOPIC_PREFIX = "/topic/game/";
    private static final String EVENTS_SUFFIX = "/events";

    /** 加入房间 */
    @MessageMapping("/game/{roomId}/join")
    public void onJoin(@DestinationVariable String roomId, @Payload GameMessage.Join msg) {
        GameRoom room = roomService.joinRoom(roomId, msg.getPlayerId(), msg.getPlayerName());
        broadcast(roomId, room, "player_join", msg.getPlayerId(), msg.getPlayerName(), null);
    }

    /** 离开房间 */
    @MessageMapping("/game/{roomId}/leave")
    public void onLeave(@DestinationVariable String roomId, @Payload GameMessage.Leave msg) {
        GameRoom room = roomService.leaveRoom(roomId, msg.getPlayerId());
        Map<String, Object> payload = new HashMap<>();
        if (room != null) {
            payload.put("newHost", room.getHostName());
        }
        broadcast(roomId, room, "player_leave", msg.getPlayerId(), null, payload);
    }

    /** 聊天 */
    @MessageMapping("/game/{roomId}/chat")
    public void onChat(@DestinationVariable String roomId, @Payload GameMessage.Chat msg) {
        ChatMessage chat = roomService.sendChat(roomId, msg.getPlayerId(), msg.getPlayerName(), msg.getContent());
        Map<String, Object> payload = new HashMap<>();
        payload.put("content", chat.getContent());
        payload.put("timestamp", chat.getTimestamp().toString());
        broadcast(roomId, roomService.getRoom(roomId), "chat", msg.getPlayerId(), msg.getPlayerName(), payload);
    }

    /** 选择角色 */
    @MessageMapping("/game/{roomId}/pick_character")
    public void onPickCharacter(@DestinationVariable String roomId, @Payload GameMessage.PickCharacter msg) {
        try {
            GameRoom room = roomService.pickCharacter(roomId, msg.getPlayerId(), msg.getCharacterId());
            Map<String, Object> payload = new HashMap<>();
            payload.put("characterId", msg.getCharacterId());
            broadcast(roomId, room, "character_pick", msg.getPlayerId(), null, payload);
        } catch (IllegalStateException e) {
            sendError(roomId, msg.getPlayerId(), e.getMessage());
        }
    }

    /** 收集线索 */
    @MessageMapping("/game/{roomId}/collect_clue")
    public void onCollectClue(@DestinationVariable String roomId, @Payload GameMessage.CollectClue msg) {
        GameRoom room = roomService.collectClue(roomId, msg.getPlayerId(), msg.getClueText());
        Map<String, Object> payload = new HashMap<>();
        payload.put("clueText", msg.getClueText());
        payload.put("collectedClues", room.getCollectedClues());
        broadcast(roomId, room, "clue_collect", msg.getPlayerId(), null, payload);
    }

    /** 切换阶段（仅房主） */
    @MessageMapping("/game/{roomId}/change_phase")
    public void onChangePhase(@DestinationVariable String roomId, @Payload GameMessage.ChangePhase msg) {
        GameRoom room = roomService.requireRoom(roomId);
        Player player = room.getPlayers().stream()
                .filter(p -> p.getPlayerId().equals(msg.getPlayerId()))
                .findFirst().orElse(null);
        if (player == null || !player.isHost()) {
            sendError(roomId, msg.getPlayerId(), "仅房主可切换阶段");
            return;
        }
        try {
            GamePhase newPhase = GamePhase.valueOf(msg.getPhase().toUpperCase());
            GameRoom updated = roomService.changePhase(roomId, newPhase);
            Map<String, Object> payload = new HashMap<>();
            payload.put("phase", newPhase.name());
            broadcast(roomId, updated, "phase_change", msg.getPlayerId(), player.getName(), payload);
        } catch (IllegalArgumentException e) {
            sendError(roomId, msg.getPlayerId(), "未知阶段：" + msg.getPhase());
        }
    }

    /** 揭示真相 */
    @MessageMapping("/game/{roomId}/reveal")
    public void onReveal(@DestinationVariable String roomId, @Payload GameMessage.Reveal msg) {
        GameRoom room = roomService.requireRoom(roomId);
        Map<String, Object> payload = new HashMap<>();
        payload.put("accusation", msg.getAccusation());
        payload.put("allClues", room.getCollectedClues());
        // 同时把房间切到 REVEAL 阶段
        GameRoom updated = roomService.changePhase(roomId, GamePhase.REVEAL);
        broadcast(roomId, updated, "reveal", msg.getPlayerId(), null, payload);
    }

    /** 广播事件 + 完整房间快照 */
    private void broadcast(String roomId, GameRoom room, String type,
                           String playerId, String playerName, Map<String, Object> payload) {
        if (payload == null) payload = new HashMap<>();
        if (room != null) payload.put("room", room);
        RoomEvent event = RoomEvent.builder()
                .type(type)
                .roomId(roomId)
                .playerId(playerId)
                .playerName(playerName)
                .payload(payload)
                .timestamp(LocalDateTime.now())
                .build();
        messaging.convertAndSend(TOPIC_PREFIX + roomId + EVENTS_SUFFIX, event);
        log.debug("广播 roomId={}, type={}, playerId={}", roomId, type, playerId);
    }

    /** 私密错误反馈：发到事件 topic，前端按 playerId 过滤 */
    private void sendError(String roomId, String playerId, String message) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("message", message);
        RoomEvent event = RoomEvent.builder()
                .type("error")
                .roomId(roomId)
                .playerId(playerId)
                .payload(payload)
                .timestamp(LocalDateTime.now())
                .build();
        messaging.convertAndSend(TOPIC_PREFIX + roomId + EVENTS_SUFFIX, event);
        log.warn("错误事件 roomId={}, playerId={}, msg={}", roomId, playerId, message);
    }
}
