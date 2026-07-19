package com.history.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 页面访问记录实体（埋点采集）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "page_views", indexes = {
    @Index(name = "idx_page_path", columnList = "page_path"),
    @Index(name = "idx_visited_at", columnList = "visited_at"),
    @Index(name = "idx_session_id", columnList = "session_id")
})
public class PageViewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 页面路径，如 /timeline、/persons */
    @Column(nullable = false, length = 128)
    private String pagePath;

    /** 用户 ID，未登录为 null */
    @Column(length = 64)
    private String userId;

    /** 会话 ID，前端 localStorage 生成 UUID，未登录用户也唯一标识 */
    @Column(nullable = false, length = 64)
    private String sessionId;

    /** 访问时间 */
    @Column(nullable = false, updatable = false)
    private LocalDateTime visitedAt;

    @PrePersist
    protected void onCreate() {
        visitedAt = LocalDateTime.now();
    }
}
