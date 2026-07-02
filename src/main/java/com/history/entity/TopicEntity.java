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
 * 专题深度长文实体
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

    @Column(nullable = false, length = 50)
    private String category;

    @Column(length = 500)
    private String coverImage;

    @Column(length = 500)
    private String summary;

    @Lob
    private String description;

    private Integer chapterCount;
    private Integer estimatedMinutes;

    @Lob
    private String tagsJson;

    @Lob
    private String relatedEventsJson;

    @Lob
    private String relatedPersonsJson;

    @Lob
    private String chapters;

    @Lob
    private String referencesJson;

    private Integer sortOrder = 0;

    private Boolean published = true;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
