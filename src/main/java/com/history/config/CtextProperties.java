package com.history.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * ctext.org API 配置
 * 申请地址：https://ctext.org/account.pl
 * 未配置 api-key 时仅能访问 searchtexts / readlink / getstatus 等公开端点
 * 配置 api-key 后可访问 gettext 获取典籍全文
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ctext")
public class CtextProperties {

    /** ctext API 基础地址 */
    private String baseUrl = "https://api.ctext.org";

    /** ctext API key（可空，未配置时访问受限） */
    private String apiKey = "";

    /** 请求超时（毫秒） */
    private int timeoutMs = 8000;

    /** 简单缓存过期时间（毫秒，默认 24 小时） */
    private long cacheTtlMs = 24 * 60 * 60 * 1000L;
}
