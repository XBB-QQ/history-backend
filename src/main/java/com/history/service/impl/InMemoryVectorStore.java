package com.history.service.impl;

import com.history.config.RagProperties;
import com.history.service.VectorStore;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 内存版向量存储 — 用 ConcurrentHashMap + 余弦相似度
 * 适用于开发/测试环境，生产环境应切换到 Redis Stack 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "rag.vector-store", havingValue = "memory", matchIfMissing = true)
public class InMemoryVectorStore implements VectorStore {

    private final RagProperties props;
    private final Map<String, VectorEntry> store = new ConcurrentHashMap<>();

    @PostConstruct
    void init() {
        log.info("使用内存向量存储（开发模式），生产环境建议配置 rag.vector-store=redis");
    }

    @Override
    public void store(String id, float[] vector, Map<String, Object> metadata) {
        store.put(id, new VectorEntry(id, vector, metadata));
    }

    @Override
    public void storeBatch(List<VectorEntry> entries) {
        for (VectorEntry entry : entries) {
            store.put(entry.id(), entry);
        }
        log.debug("批量写入 {} 条向量，总计 {}", entries.size(), store.size());
    }

    @Override
    public List<SearchResult> search(float[] queryVector, int topK) {
        List<SearchResult> results = new ArrayList<>(store.size());
        for (VectorEntry entry : store.values()) {
            float score = cosineSimilarity(queryVector, entry.vector());
            results.add(new SearchResult(entry.id(), score, entry.metadata()));
        }
        results.sort(Comparator.comparingDouble(SearchResult::score).reversed());
        return results.subList(0, Math.min(topK, results.size()));
    }

    @Override
    public void clear() {
        store.clear();
    }

    @Override
    public int size() {
        return store.size();
    }

    /**
     * 余弦相似度 — a·b / (|a|·|b|)
     */
    private static float cosineSimilarity(float[] a, float[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("向量维度不一致: " + a.length + " vs " + b.length);
        }
        float dot = 0f, normA = 0f, normB = 0f;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        float denom = (float) (Math.sqrt(normA) * Math.sqrt(normB));
        return denom == 0 ? 0f : dot / denom;
    }
}
