package com.history.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.history.config.EmbeddingProperties;
import com.history.service.EmbeddingService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Embedding 服务实现 — 调用智谱/OpenAI 兼容的 /embeddings 端点
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HttpEmbeddingService implements EmbeddingService {

    private final EmbeddingProperties props;
    private final ObjectMapper objectMapper;

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();

    @PostConstruct
    void validate() {
        if (props.getApiKey() == null || props.getApiKey().isBlank()) {
            log.warn("EMBEDDING_API_KEY 未配置，RAG 检索将不可用");
            return;
        }
        log.info("Embedding 服务已就绪：provider={}, model={}, dimensions={}",
                props.getProvider(), props.getModel(), props.getDimensions());
    }

    private static final int MAX_BATCH_SIZE = 64;

    @Override
    public float[] embed(String text) {
        return embedBatch(List.of(text)).get(0);
    }

    @Override
    public List<float[]> embedBatch(List<String> texts) {
        if (props.getApiKey() == null || props.getApiKey().isBlank()) {
            throw new IllegalStateException("EMBEDDING_API_KEY 未配置");
        }

        List<float[]> allResults = new ArrayList<>(texts.size());
        for (int i = 0; i < texts.size(); i += MAX_BATCH_SIZE) {
            List<String> batch = texts.subList(i, Math.min(i + MAX_BATCH_SIZE, texts.size()));
            allResults.addAll(callEmbeddingApi(batch));
        }
        return allResults;
    }

    private List<float[]> callEmbeddingApi(List<String> texts) {
        Map<String, Object> body = new HashMap<>();
        body.put("model", props.getModel());
        body.put("input", texts);

        String json;
        try {
            json = objectMapper.writeValueAsString(body);
        } catch (Exception e) {
            throw new RuntimeException("序列化 embedding 请求失败", e);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(props.getBaseUrl() + "/embeddings"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + props.getApiKey())
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                log.error("Embedding 上游错误 {}: {}", response.statusCode(), response.body());
                throw new RuntimeException("Embedding 服务错误 (" + response.statusCode() + ")");
            }

            JsonNode root = objectMapper.readTree(response.body());
            JsonNode data = root.path("data");
            List<float[]> results = new ArrayList<>(data.size());
            for (JsonNode item : data) {
                JsonNode embedding = item.path("embedding");
                float[] vec = new float[embedding.size()];
                for (int i = 0; i < embedding.size(); i++) {
                    vec[i] = (float) embedding.get(i).asDouble();
                }
                results.add(vec);
            }
            return results;
        } catch (Exception e) {
            throw new RuntimeException("调用 Embedding 失败: " + e.getMessage(), e);
        }
    }
}