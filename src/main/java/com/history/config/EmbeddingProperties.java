package com.history.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * Embedding 配置 — 支持智谱/OpenAI 切换（API 格式兼容）
 */
@Data
@Component
@ConfigurationProperties(prefix = "embedding")
@Validated
public class EmbeddingProperties {
    /** zhipu / openai */
    private String provider = "zhipu";
    private String baseUrl = "https://open.bigmodel.cn/api/paas/v4";
    @NotBlank(message = "EMBEDDING_API_KEY 未配置，请通过环境变量注入")
    private String apiKey;
    private String model = "embedding-3";
    private int dimensions = 1024;
}
