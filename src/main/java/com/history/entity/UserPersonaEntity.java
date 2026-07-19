package com.history.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户 AI 画像持久化实体
 * <p>
 * 将前端 personaStore 的整个 UserPersona JSON 序列化后存到后端，
 * 实现跨设备/跨浏览器同步。
 * <p>
 * 设计取舍：不拆字段（debateStances/simulatorChoices 都是数组），
 * 用 JSON 列整体存，简单直接；查询靠 username 索引。
 *
 * @see com.history.controller.UserPersonaController
 */
@Entity
@Table(name = "user_personas", indexes = {
    @Index(name = "idx_user_persona_username", columnList = "username", unique = true)
})
public class UserPersonaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 用户名（与 users.username 关联，与 favorites 表保持一致用 username 作 key） */
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /** 整个 UserPersona JSON 字符串 */
    @Lob
    @Column(columnDefinition = "TEXT", nullable = false)
    private String personaJson;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPersonaJson() { return personaJson; }
    public void setPersonaJson(String personaJson) { this.personaJson = personaJson; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
