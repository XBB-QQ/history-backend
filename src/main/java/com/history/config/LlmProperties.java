package com.history.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * LLM 代理配置 — 从环境变量读取，避免密钥泄露到代码
 */
@Data
@Component
@ConfigurationProperties(prefix = "llm")
public class LlmProperties {
    private String baseUrl = "https://open.bigmodel.cn/api/paas/v4";
    private String apiKey;
    private String model = "glm-4-flash";
}