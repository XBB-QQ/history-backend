package com.history.game.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 加入房间请求
 */
@Data
public class JoinRoomRequest {
    @NotBlank
    private String playerId;

    @NotBlank
    @Size(max = 30)
    private String playerName;
}
