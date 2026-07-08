package com.history.game.model;

/**
 * 联机剧本杀游戏阶段
 */
public enum GamePhase {
    /** 等待玩家加入 */
    WAITING,
    /** 选择角色 */
    CHARACTER,
    /** 收集线索 */
    CLUES,
    /** 审问 */
    INTERROGATE,
    /** 推理指控 */
    DEDUCTION,
    /** 揭示真相 */
    REVEAL
}
