package com.history.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户贡献内容实体 — 协作式知识贡献
 * 用户可提交事件/人物/朝代的补充或纠错，经审核后发布
 */
@Entity
@Table(name = "user_contributions")
public class ContributionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 贡献类型：event / person / dynasty / knowledge / correction */
    @Column(nullable = false, length = 20)
    private String type;

    /** 关联实体 ID（可为 null，表示新增内容） */
    private Long entityId;

    /** 标题 */
    @Column(length = 200)
    private String title;

    /** 内容（Markdown 格式） */
    @Column(columnDefinition = "TEXT")
    private String content;

    /** 变更说明（用户填写为何提交此贡献） */
    @Column(columnDefinition = "TEXT")
    private String changeDescription;

    /** 状态：pending / approved / rejected / published */
    @Column(nullable = false, length = 20)
    private String status = "pending";

    /** 提交者用户 ID */
    @Column(nullable = false)
    private Long userId;

    /** 提交者用户名（冗余，便于展示） */
    @Column(length = 50)
    private String username;

    /** 审核者管理员 ID */
    private Long reviewerId;

    /** 审核意见 */
    @Column(columnDefinition = "TEXT")
    private String reviewComment;

    /** 提交时间 */
    @Column(nullable = false)
    private LocalDateTime submittedAt;

    /** 审核时间 */
    private LocalDateTime reviewedAt;

    /** 贡献积分（审核通过后奖励） */
    private Integer points = 10;

    @PrePersist
    public void prePersist() {
        if (submittedAt == null) submittedAt = LocalDateTime.now();
    }

    // === Getters & Setters ===
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Long getEntityId() { return entityId; }
    public void setEntityId(Long entityId) { this.entityId = entityId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getChangeDescription() { return changeDescription; }
    public void setChangeDescription(String changeDescription) { this.changeDescription = changeDescription; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Long getReviewerId() { return reviewerId; }
    public void setReviewerId(Long reviewerId) { this.reviewerId = reviewerId; }

    public String getReviewComment() { return reviewComment; }
    public void setReviewComment(String reviewComment) { this.reviewComment = reviewComment; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }

    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }
}
