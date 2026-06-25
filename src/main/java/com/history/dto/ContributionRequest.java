package com.history.dto;

/**
 * 提交贡献请求
 */
public class ContributionRequest {
    private String type;        // event / person / dynasty / knowledge / correction
    private Long entityId;      // 关联实体 ID（新增内容时为 null）
    private String title;
    private String content;     // Markdown 格式
    private String changeDescription;

    // === Getters & Setters ===
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
}
