package com.history.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Wikisource API 配置
 * 端点：https://zh.wikisource.org/w/api.php
 * 无需 API key，支持 CORS，国内访问稳定
 *
 * 主要端点：
 * - action=query&list=search  搜索页面
 * - action=parse&page=xxx     获取页面 wikitext
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "wikisource")
public class WikisourceProperties {

    /** Wikisource API 基础地址 */
    private String baseUrl = "https://zh.wikisource.org/w/api.php";

    /** 请求超时（毫秒） */
    private int timeoutMs = 8000;

    /** 简单缓存过期时间（毫秒，默认 24 小时） */
    private long cacheTtlMs = 24 * 60 * 60 * 1000L;

    /** 搜索结果上限 */
    private int searchLimit = 20;
}
