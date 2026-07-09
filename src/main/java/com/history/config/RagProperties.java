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
    /** 是否在启动时自动构建 RAG 索引（默认 true） */
    private boolean autoIndex = true;
    private RedisConfig redis = new RedisConfig();

    @Data
    public static class RedisConfig {
        private String host = "localhost";
        private int port = 6379;
        private String index = "history_vectors";
    }
}
