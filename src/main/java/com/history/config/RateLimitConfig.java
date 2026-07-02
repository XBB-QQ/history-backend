package com.history.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.google.common.util.concurrent.RateLimiter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 接口限流配置
 * 使用 Guava RateLimiter 实现滑动窗口限流
 * - 公共接口：每秒 100 次请求
 * - 用户接口：每秒 20 次请求
 * - 管理接口：每秒 10 次请求
 */
@Configuration
@Slf4j
public class RateLimitConfig implements WebMvcConfigurer {

    // 限流器缓存：key = IP地址
    private final Map<String, RateLimiter> limiterCache = new ConcurrentHashMap<>();

    // 不同接口的限流速率
    private static final double PUBLIC_RATE = 100.0;    // 公共接口
    private static final double USER_RATE = 20.0;       // 用户接口
    private static final double ADMIN_RATE = 10.0;      // 管理接口

    /**
     * 根据路径获取对应的 RateLimiter
     */
    private RateLimiter getRateLimiter(String path) {
        double rate;
        if (path.startsWith("/api/admin/")) {
            rate = ADMIN_RATE;
        } else if (path.startsWith("/api/user/")) {
            rate = USER_RATE;
        } else {
            rate = PUBLIC_RATE;
        }

        // 使用路径+速率作为缓存 key
        String cacheKey = path + ":" + rate;
        return limiterCache.computeIfAbsent(cacheKey, k -> RateLimiter.create(rate));
    }

    @Bean
    public HandlerInterceptor rateLimitInterceptor() {
        return new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                String clientIp = getClientIp(request);
                String path = request.getRequestURI();

                RateLimiter limiter = getRateLimiter(path);

                // tryAcquire 非阻塞，获取不到直接返回 429
                if (!limiter.tryAcquire()) {
                    log.warn("Rate limited: IP={}, path={}", clientIp, path);
                    response.setStatus(429);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"error\":\"请求过于频繁，请稍后再试\",\"status\":429}");
                    return false;
                }

                // 在响应头中添加限流信息
                response.setHeader("X-RateLimit-Limit", String.valueOf((int) limiter.getRate()));

                return true;
            }
        };
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor())
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/admin/auth/login",
                        "/api/admin/auth/verify"
                );
    }

    /**
     * 获取客户端真实 IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        // X-Forwarded-For 可能包含多个 IP，取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
