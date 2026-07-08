package com.history.service;

import java.util.List;

/**
 * Embedding 服务 — 将文本转为向量
 */
public interface EmbeddingService {

    /**
     * 单条文本嵌入
     */
    float[] embed(String text);

    /**
     * 批量嵌入
     */
    List<float[]> embedBatch(List<String> texts);
}
