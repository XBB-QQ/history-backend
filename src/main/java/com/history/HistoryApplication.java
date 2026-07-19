package com.history;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class HistoryApplication {
    public static void main(String[] args) {
        // 强制使用 IPv4，避免某些环境 IPv6 路由不通导致外部 API 连接超时
        // 必须在 JVM 启动时（即任何网络调用前）设置，运行时设置无效
        // 影响：zh.wikisource.org、api.ctext.org 等同时返回 A/AAAA 记录的站点
        System.setProperty("java.net.preferIPv4Stack", "true");
        System.setProperty("java.net.preferIPv6Addresses", "false");
        SpringApplication.run(HistoryApplication.class, args);
    }
}
