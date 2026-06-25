package com.history.dto;

import java.time.LocalDateTime;

public class ContributionDTO {
    private Long id;
    private String type;
    private Long entityId;
    private String title;
    private String content;
    private String changeDescription;
    private String status;
    private Long userId;
    private String username;
    private Long reviewerId;
    private String reviewComment;
    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;
    private Integer points;

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
