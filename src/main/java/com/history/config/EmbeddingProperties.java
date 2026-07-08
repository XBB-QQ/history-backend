package com.history.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Embedding 配置 — 支持智谱/OpenAI 切换（API 格式兼容）
 */
@Data
@Component
@ConfigurationProperties(prefix = "embedding")
public class EmbeddingProperties {
    /** zhipu / openai */
    private String provider = "zhipu";
    private String baseUrl = "https://open.bigmodel.cn/api/paas/v4";
    private String apiKey;
    private String model = "embedding-3";
    private int dimensions = 1024;
}
