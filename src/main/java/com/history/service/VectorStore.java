package com.history.service;

import java.util.List;
import java.util.Map;

/**
 * 向量存储 — 抽象接口，支持内存/Redis 切换
 */
public interface VectorStore {

    /**
     * 存储向量
     * @param id 文档 ID（如 event:1, person:2）
     * @param vector 嵌入向量
     * @param metadata 元数据（type, title, content, source 等）
     */
    void store(String id, float[] vector, Map<String, Object> metadata);

    /**
     * 批量存储
     */
    void storeBatch(List<VectorEntry> entries);

    /**
     * 相似度检索
     * @param queryVector 查询向量
     * @param topK 返回前 K 条
     * @return 按相似度降序排列的结果
     */
    List<SearchResult> search(float[] queryVector, int topK);

    /**
     * 清空所有向量
     */
    void clear();

    /**
     * 向量数量
     */
    int size();

    /**
     * 向量条目
     */
    record VectorEntry(String id, float[] vector, Map<String, Object> metadata) {}

    /**
     * 检索结果
     */
    record SearchResult(String id, float score, Map<String, Object> metadata) {}
}
