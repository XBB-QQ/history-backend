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
 * 知识卡片实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "knowledge_cards")
public class KnowledgeCardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String uid;

    @Column(nullable = false, length = 200)
    private String title;

    /** 起始年份 */
    private Integer startYear;

    @Column(length = 100)
    private String startYearDisplay;

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
    private java.util.List<String> relevantEvents = new java.util.ArrayList<>();

    @ElementCollection
    @Builder.Default
    private java.util.List<String> relevantPersons = new java.util.ArrayList<>();

    /** 元数据（JSON 字符串） */
    @Column(length = 2000)
    private String meta;

    @Column(length = 500)
    private String source;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime crawlDate;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
