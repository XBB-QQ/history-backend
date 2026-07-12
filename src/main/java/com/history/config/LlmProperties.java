package com.history.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * LLM 代理配置 — 从环境变量读取，避免密钥泄露到代码
 */
@Data
@Component
@ConfigurationProperties(prefix = "llm")
@Validated
public class LlmProperties {
    private String baseUrl = "https://open.bigmodel.cn/api/paas/v4";
    @NotBlank(message = "LLM_API_KEY 未配置，请通过环境变量注入")
    private String apiKey;
    private String model = "glm-4-flash";
}
