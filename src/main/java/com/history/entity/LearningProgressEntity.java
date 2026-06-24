package com.history.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户学习进度 — 记录用户浏览过的资源
 */
@Entity
@Table(name = "learning_progress")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LearningProgressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    /** 资源类型：event/person/dynasty/knowledge */
    @Column(name = "resource_type", nullable = false, length = 20)
    private String resourceType;

    /** 资源 ID */
    @Column(name = "resource_id", nullable = false)
    private Long resourceId;

    /** 浏览次数 */
    @Column(name = "view_count", nullable = false)
    private int viewCount = 1;

    @Column(name = "last_viewed", nullable = false)
    private LocalDateTime lastViewed;

    @PrePersist
    protected void onCreate() {
        lastViewed = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastViewed = LocalDateTime.now();
    }
}
