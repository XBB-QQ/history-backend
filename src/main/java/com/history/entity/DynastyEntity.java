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
 * 朝代实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "dynasties", indexes = {
    @Index(name = "idx_dynasty_start", columnList = "periodStart")
})
public class DynastyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String uid;

    @Column(nullable = false, length = 50)
    private String name;

    /** 全称（如 唐朝） */
    @Column(length = 100)
    private String fullName;

    /** 时期范围（如 公元前2070年—公元前1046年） */
    @Column(length = 200)
    private String period;

    /** 起始年，null 表示不详 */
    private Integer periodStart;

    /** 结束年 */
    private Integer periodEnd;

    @Column(length = 100)
    private String founder;

    @Column(length = 100)
    private String lastRuler;

    @Column(length = 200)
    private String capital;

    @Column(length = 50)
    private String duration;

    @Column(length = 200)
    private String highlights;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 500)
    private String fallReason;

    @Column(columnDefinition = "TEXT")
    private String legacy;

    /** 人口峰值（如 约5200万） */
    @Column(length = 100)
    private String populationPeak;

    /** GDP 估算 */
    @Column(length = 100)
    private String gdpEstimate;

    /** 主要贸易路线 */
    @Column(length = 500)
    private String majorTradeRoutes;

    /** 文化亮点 */
    @Column(columnDefinition = "TEXT")
    private String culturalHighlights;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
