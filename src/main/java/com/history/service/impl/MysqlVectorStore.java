package com.history.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.history.entity.RagVectorEntity;
import com.history.repository.RagVectorRepository;
import com.history.service.VectorStore;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MySQL 持久化向量存储
 * <p>
 * 启动时从 rag_vectors 表一次性加载所有向量到内存 ConcurrentHashMap，
 * 检索时用余弦相似度（与 InMemoryVectorStore 一致），
 * 写入时同步持久化到 MySQL。
 * <p>
 * 优点：
 * 1. 重启后端不重新调用 embedding API（核心痛点解决）
 * 2. 检索性能等同内存版（数据量 ~100 条，brute-force 完全可接受）
 * 3. 零新依赖（MySQL 已有）
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "rag.vector-store", havingValue = "mysql", matchIfMissing = true)
public class MysqlVectorStore implements VectorStore {

    private final RagVectorRepository repository;
    private final ObjectMapper objectMapper;

    /** 内存缓存，启动时从 DB 加载，检索时直接用 */
    private final Map<String, VectorEntry> store = new ConcurrentHashMap<>();

    @PostConstruct
    void init() {
        List<RagVectorEntity> rows = repository.findAll();
        int loaded = 0;
        for (RagVectorEntity row : rows) {
            try {
                float[] vector = bytesToFloats(row.getVector());
                Map<String, Object> metadata = parseMetadata(row.getMetadata());
                store.put(row.getId(), new VectorEntry(row.getId(), vector, metadata));
                loaded++;
            } catch (Exception e) {
                log.warn("加载向量 {} 失败: {}", row.getId(), e.getMessage());
            }
        }
        log.info("使用 MySQL 持久化向量存储，启动加载 {} 条向量", loaded);
    }

    @Override
    public void store(String id, float[] vector, Map<String, Object> metadata) {
        store.put(id, new VectorEntry(id, vector, metadata));
        persist(id, vector, metadata);
    }

    @Override
    public void storeBatch(List<VectorEntry> entries) {
        List<RagVectorEntity> entities = new ArrayList<>(entries.size());
        for (VectorEntry entry : entries) {
            store.put(entry.id(), entry);
            entities.add(toEntity(entry));
        }
        repository.saveAll(entities);
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
        repository.deleteAllInBatch();
    }

    @Override
    public int size() {
        return store.size();
    }

    // ---------- 私有辅助方法 ----------

    private void persist(String id, float[] vector, Map<String, Object> metadata) {
        try {
            repository.save(toEntity(new VectorEntry(id, vector, metadata)));
        } catch (Exception e) {
            log.error("持久化向量 {} 失败: {}", id, e.getMessage());
        }
    }

    private RagVectorEntity toEntity(VectorEntry entry) {
        try {
            return new RagVectorEntity(
                    entry.id(),
                    floatsToBytes(entry.vector()),
                    objectMapper.writeValueAsString(entry.metadata())
            );
        } catch (Exception e) {
            throw new RuntimeException("序列化向量元数据失败: " + entry.id(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseMetadata(String json) throws Exception {
        if (json == null || json.isBlank()) {
            return Map.of();
        }
        return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
    }

    /** float[] → byte[]（小端序，与 Redis/JVM 默认一致） */
    private static byte[] floatsToBytes(float[] floats) {
        ByteBuffer buffer = ByteBuffer.allocate(floats.length * Float.BYTES)
                .order(ByteOrder.LITTLE_ENDIAN);
        for (float f : floats) {
            buffer.putFloat(f);
        }
        return buffer.array();
    }

    /** byte[] → float[] */
    private static float[] bytesToFloats(byte[] bytes) {
        if (bytes.length % Float.BYTES != 0) {
            throw new IllegalArgumentException("字节数长度不是 4 的倍数: " + bytes.length);
        }
        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        float[] floats = new float[bytes.length / Float.BYTES];
        for (int i = 0; i < floats.length; i++) {
            floats[i] = buffer.getFloat();
        }
        return floats;
    }

    /** 余弦相似度 — a·b / (|a|·|b|) */
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
