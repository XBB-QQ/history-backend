package com.history.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * RAG 配置 — 向量库类型、检索参数、Redis 连接
 */
@Data
@Component
@ConfigurationProperties(prefix = "rag")
public class RagProperties {
    /** memory / redis */
    private String vectorStore = "memory";
    private int topK = 5;
    private RedisConfig redis = new RedisConfig();

    @Data
    public static class RedisConfig {
        private String host = "localhost";
        private int port = 6379;
        private String index = "history_vectors";
    }
}
