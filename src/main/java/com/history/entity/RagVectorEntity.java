package com.history.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * RAG 向量持久化实体
 * <p>
 * 用于将向量从内存迁移到 MySQL，避免每次重启后端都调用 embedding API 重建索引。
 * 向量以 BLOB 存储（float[] 的二进制形式），元数据以 JSON 字符串存储。
 */
@Entity
@Table(name = "rag_vectors")
public class RagVectorEntity {

    /** 向量唯一标识（如 event-332, person-57, dynasty-1, knowledge-10） */
    @Id
    @Column(length = 128)
    private String id;

    /** float[] 的二进制数据，每 float 4 字节，1024 维 = 4096 字节。
     *  显式指定 LONGBLOB：Hibernate 6 + MySQL 8 对 @Lob byte[] 默认映射为 TINYBLOB（255 字节）装不下 1024 维向量 */
    @Lob
    @Column(nullable = false, columnDefinition = "LONGBLOB")
    private byte[] vector;

    /** JSON 元数据字符串（type/title/content/source/year 等） */
    @Column(columnDefinition = "TEXT")
    private String metadata;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public RagVectorEntity() {
    }

    public RagVectorEntity(String id, byte[] vector, String metadata) {
        this.id = id;
        this.vector = vector;
        this.metadata = metadata;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public byte[] getVector() {
        return vector;
    }

    public void setVector(byte[] vector) {
        this.vector = vector;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
