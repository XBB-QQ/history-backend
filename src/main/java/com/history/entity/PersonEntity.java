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
 * 历史人物实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "persons", indexes = {
    @Index(name = "idx_person_name", columnList = "name"),
    @Index(name = "idx_person_gender", columnList = "gender")
})
public class PersonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String uid;

    @Column(nullable = false, length = 100)
    private String name;

    /** 字/号 */
    @Column(length = 100)
    private String courtesyName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dynasty_id")
    private DynastyEntity dynasty;

    /** 生卒年 [birth, death]，公元前为负，null 表示未知 */
    @ElementCollection
    @CollectionTable(name = "person_years", joinColumns = @JoinColumn(name = "person_id"))
    @Column
    private java.util.List<Integer> years;

    @Column(length = 100)
    private String yearsDisplay;

    /** male / female / unknown */
    @Column(length = 20)
    private String gender;

    @ElementCollection
    @Builder.Default
    private java.util.List<String> roles = new java.util.ArrayList<>();

    @Column(length = 500)
    private String quote;

    @Column(columnDefinition = "TEXT")
    private String bio;

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

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime crawlDate;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
