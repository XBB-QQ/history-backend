package com.history.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 历史事件实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "events", indexes = {
    @Index(name = "idx_event_year", columnList = "year"),
    @Index(name = "idx_event_category", columnList = "category"),
    @Index(name = "idx_event_dynasty", columnList = "dynasty_id")
})
public class EventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 全局唯一标识（如 qin-unify） */
    @Column(nullable = false, unique = true, length = 64)
    private String uid;

    @Column(nullable = false, length = 200)
    private String title;

    /** 年份数字，公元前为负 */
    @Column(nullable = false)
    private Integer year;

    /** 人类可读年份（如 公元前221年） */
    @Column(length = 100)
    private String yearDisplay;

    /** exact / approx / range */
    @Column(length = 20)
    private String yearPrecision;

    /** 分类：朝代更迭/战争/改革/文化/盛世/屈辱/革命/经济 */
    @Column(nullable = false, length = 50)
    private String category;

    /** 所属朝代 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dynasty_id")
    private DynastyEntity dynasty;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String fulltext;

    @ElementCollection
    @Builder.Default
    private java.util.List<String> tags = new java.util.ArrayList<>();

    @ElementCollection
    @Builder.Default
    private java.util.List<String> relatedEvents = new java.util.ArrayList<>();

    @ElementCollection
    @Builder.Default
    private java.util.List<String> relatedPersons = new java.util.ArrayList<>();

    @Column(length = 500)
    private String source;

    /** 影响分析 */
    @Column(columnDefinition = "TEXT")
    private String impact;

    /** 重要性评级 1-5 */
    @Column
    private Integer significance = 3;

    /** 相关文章链接 */
    @ElementCollection
    @Builder.Default
    private java.util.List<String> relatedArticles = new java.util.ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime crawlDate;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
