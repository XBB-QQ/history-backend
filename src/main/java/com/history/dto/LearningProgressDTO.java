package com.history.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 学习进度记录
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LearningProgressDTO {
    private Long id;
    private String resourceType;
    private Long resourceId;
    private int viewCount;
    private String resourceName;
    private LocalDateTime lastViewed;
}
