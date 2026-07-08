package com.history.game;

import com.history.game.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 联机剧本杀房间服务
 * - 内存 ConcurrentHashMap 管理，重启即丢失（会话级数据，符合产品定位）
 * - 所有写操作返回 RoomEvent 供 Controller 广播
 */
@Slf4j
@Service
public class GameRoomService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String ROOM_ID_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    private final Map<String, GameRoom> rooms = new ConcurrentHashMap<>();

    /** 创建房间，返回新建的房间 */
    public GameRoom createRoom(String scriptId, String hostName, String hostPlayerId) {
        String roomId = generateRoomId();
        GameRoom room = GameRoom.builder()
                .roomId(roomId)
                .scriptId(scriptId)
                .hostName(hostName)
                .phase(GamePhase.WAITING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Player host = Player.builder()
                .playerId(hostPlayerId)
                .name(hostName)
                .isHost(true)
                .joinedAt(LocalDateTime.now())
                .build();
        room.getPlayers().add(host);
        rooms.put(roomId, room);
        log.info("房间创建：roomId={}, scriptId={}, host={}", roomId, scriptId, hostName);
        return room;
    }

    /** 加入房间，返回加入后的房间；不存在抛 IllegalStateException */
    public GameRoom joinRoom(String roomId, String playerId, String playerName) {
        GameRoom room = requireRoom(roomId);
        synchronized (room) {
            boolean exists = room.getPlayers().stream().anyMatch(p -> p.getPlayerId().equals(playerId));
            if (!exists) {
                room.getPlayers().add(Player.builder()
                        .playerId(playerId)
                        .name(playerName)
                        .isHost(false)
                        .joinedAt(LocalDateTime.now())
                        .build());
                room.setUpdatedAt(LocalDateTime.now());
            }
        }
        log.info("玩家加入：roomId={}, playerId={}, name={}", roomId, playerId, playerName);
        return room;
    }

    /** 离开房间，返回更新后的房间（房间空则删除并返回 null） */
    public GameRoom leaveRoom(String roomId, String playerId) {
        GameRoom room = rooms.get(roomId);
        if (room == null) return null;
        synchronized (room) {
            room.getPlayers().removeIf(p -> p.getPlayerId().equals(playerId));
            room.getCharacterAssignments().remove(playerId);
            room.setUpdatedAt(LocalDateTime.now());
            if (room.getPlayers().isEmpty()) {
                rooms.remove(roomId);
                log.info("房间销毁（无玩家）：roomId={}", roomId);
                return null;
            }
            // 若房主离开，提升第一个玩家为新房主
            if (room.getPlayers().stream().noneMatch(Player::isHost) && !room.getPlayers().isEmpty()) {
                room.getPlayers().get(0).setHost(true);
                room.setHostName(room.getPlayers().get(0).getName());
            }
        }
        log.info("玩家离开：roomId={}, playerId={}", roomId, playerId);
        return room;
    }

    public GameRoom getRoom(String roomId) {
        return rooms.get(roomId);
    }

    public GameRoom requireRoom(String roomId) {
        GameRoom room = rooms.get(roomId);
        if (room == null) {
            throw new IllegalStateException("房间不存在或已关闭：" + roomId);
        }
        return room;
    }

    /** 选择角色（同一角色不能被多人选） */
    public GameRoom pickCharacter(String roomId, String playerId, String characterId) {
        GameRoom room = requireRoom(roomId);
        synchronized (room) {
            boolean charTaken = room.getCharacterAssignments().values().stream()
                    .anyMatch(cid -> cid.equals(characterId));
            if (charTaken) {
                throw new IllegalStateException("该角色已被其他玩家选择：" + characterId);
            }
            room.getCharacterAssignments().put(playerId, characterId);
            room.setUpdatedAt(LocalDateTime.now());
        }
        return room;
    }

    /** 收集线索（去重，返回的房间用于判断是否新增） */
    public GameRoom collectClue(String roomId, String playerId, String clueText) {
        GameRoom room = requireRoom(roomId);
        synchronized (room) {
            room.getCollectedClues().add(clueText);
            room.setUpdatedAt(LocalDateTime.now());
        }
        return room;
    }

    /** 切换阶段（仅房主可操作，由 Controller 校验） */
    public GameRoom changePhase(String roomId, GamePhase newPhase) {
        GameRoom room = requireRoom(roomId);
        synchronized (room) {
            room.setPhase(newPhase);
            room.setUpdatedAt(LocalDateTime.now());
        }
        return room;
    }

    /** 发送聊天消息，返回新增的 ChatMessage */
    public ChatMessage sendChat(String roomId, String playerId, String playerName, String content) {
        GameRoom room = requireRoom(roomId);
        ChatMessage msg = ChatMessage.builder()
                .playerId(playerId)
                .playerName(playerName)
                .content(content)
                .timestamp(LocalDateTime.now())
                .build();
        synchronized (room) {
            room.getChatHistory().add(msg);
            room.setUpdatedAt(LocalDateTime.now());
        }
        return msg;
    }

    /** 列出所有房间（调试用） */
    public List<GameRoom> listRooms() {
        return List.copyOf(rooms.values());
    }

    private String generateRoomId() {
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            sb.append(ROOM_ID_CHARS.charAt(RANDOM.nextInt(ROOM_ID_CHARS.length())));
        }
        String id = sb.toString();
        // 极小概率重复，重试一次
        if (rooms.containsKey(id)) return generateRoomId();
        return id;
    }
}
