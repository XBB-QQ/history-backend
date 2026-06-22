package com.history.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 收藏 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteDTO {
    private Long id;
    private String userId;
    private String resourceType;
    private Long resourceId;
    private String title;
    private Boolean pinned;
    private LocalDateTime createdAt;
}
