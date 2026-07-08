package com.history.game;

import com.history.game.model.GamePhase;
import com.history.game.model.GameRoom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * GameRoomService 单元测试 — 不依赖 Spring 上下文，纯逻辑验证。
 */
class GameRoomServiceTest {

    private GameRoomService service;

    @BeforeEach
    void setUp() {
        service = new GameRoomService();
    }

    @Test
    void createRoom_returnsRoomWithHostAnd6CharCode() {
        GameRoom room = service.createRoom("shang-mystery", "Alice", "p1");
        assertEquals("shang-mystery", room.getScriptId());
        assertEquals("Alice", room.getHostName());
        assertEquals(6, room.getRoomId().length());
        assertEquals(GamePhase.WAITING, room.getPhase());
        assertEquals(1, room.getPlayers().size());
        assertTrue(room.getPlayers().get(0).isHost());
        assertEquals("p1", room.getPlayers().get(0).getPlayerId());
    }

    @Test
    void joinRoom_addsNewPlayer() {
        GameRoom room = service.createRoom("shang-mystery", "Alice", "p1");
        GameRoom updated = service.joinRoom(room.getRoomId(), "p2", "Bob");
        assertEquals(2, updated.getPlayers().size());
        assertEquals("Bob", updated.getPlayers().get(1).getName());
        assertFalse(updated.getPlayers().get(1).isHost());
    }

    @Test
    void joinRoom_idempotent_samePlayerJoinTwice() {
        GameRoom room = service.createRoom("shang-mystery", "Alice", "p1");
        service.joinRoom(room.getRoomId(), "p1", "Alice");
        GameRoom updated = service.getRoom(room.getRoomId());
        assertEquals(1, updated.getPlayers().size());
    }

    @Test
    void joinRoom_nonExistentRoom_throws() {
        assertThrows(IllegalStateException.class,
                () -> service.joinRoom("FAKE12", "p2", "Bob"));
    }

    @Test
    void leaveRoom_lastPlayerDestroysRoom() {
        GameRoom room = service.createRoom("shang-mystery", "Alice", "p1");
        GameRoom result = service.leaveRoom(room.getRoomId(), "p1");
        assertNull(result);
        assertNull(service.getRoom(room.getRoomId()));
    }

    @Test
    void leaveRoom_hostLeaves_promotesNextPlayer() {
        GameRoom room = service.createRoom("shang-mystery", "Alice", "p1");
        service.joinRoom(room.getRoomId(), "p2", "Bob");
        service.leaveRoom(room.getRoomId(), "p1");
        GameRoom updated = service.getRoom(room.getRoomId());
        assertEquals(1, updated.getPlayers().size());
        assertTrue(updated.getPlayers().get(0).isHost());
        assertEquals("Bob", updated.getHostName());
    }

    @Test
    void pickCharacter_assignsCharacterToPlayer() {
        GameRoom room = service.createRoom("shang-mystery", "Alice", "p1");
        service.pickCharacter(room.getRoomId(), "p1", "wei-wang-xian-jia");
        GameRoom updated = service.getRoom(room.getRoomId());
        assertEquals("wei-wang-xian-jia", updated.getCharacterAssignments().get("p1"));
    }

    @Test
    void pickCharacter_duplicateThrows() {
        GameRoom room = service.createRoom("shang-mystery", "Alice", "p1");
        service.joinRoom(room.getRoomId(), "p2", "Bob");
        service.pickCharacter(room.getRoomId(), "p1", "pan-geng-wo");
        assertThrows(IllegalStateException.class,
                () -> service.pickCharacter(room.getRoomId(), "p2", "pan-geng-wo"));
    }

    @Test
    void collectClue_deduplicates() {
        GameRoom room = service.createRoom("shang-mystery", "Alice", "p1");
        service.collectClue(room.getRoomId(), "p1", "西夷使者的信件");
        service.collectClue(room.getRoomId(), "p1", "西夷使者的信件");
        GameRoom updated = service.getRoom(room.getRoomId());
        assertEquals(1, updated.getCollectedClues().size());
    }

    @Test
    void changePhase_updatesPhase() {
        GameRoom room = service.createRoom("shang-mystery", "Alice", "p1");
        service.changePhase(room.getRoomId(), GamePhase.CHARACTER);
        GameRoom updated = service.getRoom(room.getRoomId());
        assertEquals(GamePhase.CHARACTER, updated.getPhase());
    }

    @Test
    void sendChat_appendsMessage() {
        GameRoom room = service.createRoom("shang-mystery", "Alice", "p1");
        service.sendChat(room.getRoomId(), "p1", "Alice", "hello");
        GameRoom updated = service.getRoom(room.getRoomId());
        assertEquals(1, updated.getChatHistory().size());
        assertEquals("hello", updated.getChatHistory().get(0).getContent());
    }

    @Test
    void listRooms_returnsAllActiveRooms() {
        GameRoom r1 = service.createRoom("shang-mystery", "Alice", "p1");
        GameRoom r2 = service.createRoom("shang-mystery", "Bob", "p2");
        assertEquals(2, service.listRooms().size());
        assertNotEquals(r1.getRoomId(), r2.getRoomId());
    }
}
