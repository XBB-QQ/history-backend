package com.history.game.dto;

import lombok.Data;

/**
 * STOMP 消息载体集合（一个文件装齐，避免碎片化）
 */
public class GameMessage {

    @Data
    public static class Join {
        private String playerId;
        private String playerName;
    }

    @Data
    public static class Leave {
        private String playerId;
    }

    @Data
    public static class Chat {
        private String playerId;
        private String playerName;
        private String content;
    }

    @Data
    public static class PickCharacter {
        private String playerId;
        private String characterId;
    }

    @Data
    public static class CollectClue {
        private String playerId;
        private String clueText;
    }

    @Data
    public static class ChangePhase {
        private String playerId;
        private String phase; // GamePhase.name()
    }

    @Data
    public static class Reveal {
        private String playerId;
        private String accusation;
    }
}
