package com.history.game;

import com.history.game.dto.CreateRoomRequest;
import com.history.game.dto.JoinRoomRequest;
import com.history.game.model.GameRoom;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 联机剧本杀 HTTP API
 * - 创建房间、查询房间状态、加入房间（备用，主要走 WS）
 * - STOMP 消息处理见 {@link GameStompController}
 */
@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
@Tag(name = "联机剧本杀", description = "T101 房间管理 + WebSocket 信令")
public class GameRoomController {

    private final GameRoomService roomService;

    /** 创建房间 */
    @PostMapping("/rooms")
    @Operation(summary = "创建房间，返回房间号")
    public ResponseEntity<GameRoom> createRoom(@Valid @RequestBody CreateRoomRequest req) {
        GameRoom room = roomService.createRoom(req.getScriptId(), req.getHostName(), req.getHostPlayerId());
        return ResponseEntity.ok(room);
    }

    /** 查询房间状态 */
    @GetMapping("/rooms/{roomId}")
    @Operation(summary = "查询房间状态")
    public ResponseEntity<GameRoom> getRoom(@PathVariable String roomId) {
        GameRoom room = roomService.getRoom(roomId);
        if (room == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(room);
    }

    /** HTTP 加入（备用，主流程走 STOMP /app/game/{roomId}/join） */
    @PostMapping("/rooms/{roomId}/join")
    @Operation(summary = "HTTP 加入房间（备用）")
    public ResponseEntity<GameRoom> joinRoom(@PathVariable String roomId,
                                              @Valid @RequestBody JoinRoomRequest req) {
        GameRoom room = roomService.joinRoom(roomId, req.getPlayerId(), req.getPlayerName());
        return ResponseEntity.ok(room);
    }

    /** 调试：列出所有房间 */
    @GetMapping("/rooms")
    @Operation(summary = "列出所有房间（调试用）")
    public ResponseEntity<java.util.List<GameRoom>> listRooms() {
        return ResponseEntity.ok(roomService.listRooms());
    }
}
