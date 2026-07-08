package com.history.game.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建房间请求
 */
@Data
public class CreateRoomRequest {
    @NotBlank
    private String scriptId;

    @NotBlank
    @Size(max = 30)
    private String hostName;

    @NotBlank
    private String hostPlayerId;
}
