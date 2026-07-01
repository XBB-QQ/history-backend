package com.history.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 专题深度长文实体
 * 每个专题包含多个章节，支持分段阅读和进度追踪
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "topics")
public class TopicEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String uid;

    @Column(nullable = false, length = 200)
    private String title;

    /** 专题分类：制度/经济/军事/文化 */
    @Column(nullable = false, length = 50)
    private String category;

    /** 专题封面图片URL */
    @Column(length = 500)
    private String coverImage;

    /** 专题简介（一句话概述） */
    @Column(length = 500)
    private String summary;

    /** 专题详细描述 */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** 阅读所需章节数 */
    private Integer chapterCount;

    /** 预计阅读时间（分钟） */
    private Integer estimatedMinutes;

    /** 标签 */
    @ElementCollection
    @Builder.Default
    private List<String> tags = new java.util.ArrayList<>();

    /** 关联事件UID列表 */
    @ElementCollection
    @Builder.Default
    private List<String> relatedEvents = new java.util.ArrayList<>();

    /** 关联人物UID列表 */
    @ElementCollection
    @Builder.Default
    private List<String> relatedPersons = new java.util.ArrayList<>();

    /** 章节列表（JSON TEXT） */
    @Column(columnDefinition = "TEXT")
    private String chapters;

    /** 参考资料列表 */
    @ElementCollection
    @Builder.Default
    private List<String> references = new java.util.ArrayList<>();

    /** 排序权重 */
    private Integer sortOrder = 0;

    /** 是否公开 */
    @Builder.Default
    private Boolean published = true;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
