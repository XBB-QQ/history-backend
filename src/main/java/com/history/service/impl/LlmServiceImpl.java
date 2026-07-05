package com.history.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.history.config.LlmProperties;
import com.history.dto.LlmChatRequest;
import com.history.service.LlmService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * LLM 代理服务实现 — 使用 JDK 11+ HttpClient 调用智谱 AI
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LlmServiceImpl implements LlmService {

    private final LlmProperties props;
    private final ObjectMapper objectMapper;

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @PostConstruct
    void validate() {
        if (props.getApiKey() == null || props.getApiKey().isBlank()) {
            throw new IllegalStateException("LLM_API_KEY 环境变量未配置，请在启动时设置 LLM_API_KEY");
        }
        log.info("LLM 代理已就绪：baseUrl={}, model={}", props.getBaseUrl(), props.getModel());
    }

    @Override
    public String chat(LlmChatRequest request) {
        Map<String, Object> body = buildRequestBody(request, false);
        String json = writeJson(body);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(props.getBaseUrl() + "/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + props.getApiKey())
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        try {
            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                log.error("LLM 上游错误 {}: {}", response.statusCode(), response.body());
                throw new RuntimeException("LLM 服务错误 (" + response.statusCode() + "): " + response.body());
            }
            JsonNode root = objectMapper.readTree(response.body());
            return root.path("choices").path(0).path("message").path("content").asText("");
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("调用 LLM 失败", e);
        }
    }

    @Override
    public void chatStream(LlmChatRequest request, OutputStream outputStream) {
        Map<String, Object> body = buildRequestBody(request, true);
        String json = writeJson(body);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(props.getBaseUrl() + "/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + props.getApiKey())
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        try {
            HttpResponse<java.io.InputStream> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofInputStream());
            if (response.statusCode() >= 400) {
                String errBody = new String(response.body().readAllBytes());
                log.error("LLM 流式上游错误 {}: {}", response.statusCode(), errBody);
                throw new RuntimeException("LLM 流式服务错误 (" + response.statusCode() + "): " + errBody);
            }
            try (var is = response.body()) {
                is.transferTo(outputStream);
                outputStream.flush();
            }
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("调用 LLM 流式失败", e);
        }
    }

    private Map<String, Object> buildRequestBody(LlmChatRequest request, boolean stream) {
        Map<String, Object> body = new HashMap<>();
        body.put("messages", request.getMessages());
        body.put("model", request.getModel() != null ? request.getModel() : props.getModel());
        body.put("max_tokens", request.getMaxTokens() != null ? request.getMaxTokens() : 2048);
        body.put("temperature", request.getTemperature() != null ? request.getTemperature() : 0.7);
        if (stream) {
            body.put("stream", true);
        }
        return body;
    }

    private String writeJson(Map<String, Object> body) {
        try {
            return objectMapper.writeValueAsString(body);
        } catch (IOException e) {
            throw new RuntimeException("序列化请求失败", e);
        }
    }
}
