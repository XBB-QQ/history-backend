package com.history.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户收藏实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "favorites")
public class FavoriteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 用户 ID（预留，目前用 anonymous） */
    @Column(nullable = false, length = 64)
    private String userId;

    /** 被收藏的资源类型 */
    @Column(nullable = false, length = 16)
    private String resourceType; // event, person, dynasty, knowledge

    /** 被收藏的资源 ID */
    @Column(nullable = false)
    private Long resourceId;

    /** 标题快照 */
    @Column(nullable = false, length = 200)
    private String title;

    /** 是否置顶 */
    @Column(nullable = false)
    private Boolean pinned = false;

    /** 创建时间 */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
