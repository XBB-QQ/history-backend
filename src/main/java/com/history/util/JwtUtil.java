package com.history.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT 工具类
 */
public class JwtUtil {

    // JWT 签名密钥获取优先级：
    // 1. 环境变量 JWT_SECRET
    // 2. JVM 系统属性 -DJWT_SECRET=...
    // 3. 开发环境默认值（仅 dev profile 启用，prod 缺失则抛异常终止启动）
    private static final String SECRET;

    static {
        String secret = System.getenv("JWT_SECRET");
        if (secret == null || secret.isBlank()) {
            secret = System.getProperty("JWT_SECRET");
        }
        if (secret == null || secret.isBlank()) {
            // 检测当前 profile：prod 必须显式配置 JWT_SECRET，否则终止启动
            String profile = System.getProperty("spring.profiles.active", "");
            if ("prod".equalsIgnoreCase(profile)) {
                throw new IllegalStateException(
                    "生产环境必须通过 JWT_SECRET 环境变量配置 JWT 签名密钥（不少于 32 字符）");
            }
            // dev/test 兜底（仅本地开发，不可用于生产）
            secret = "history-museum-dev-jwt-secret-key-2024-do-not-use-in-production";
        }
        if (secret.length() < 32) {
            throw new RuntimeException("JWT_SECRET 长度不能少于 32 个字符！");
        }
        SECRET = secret;
    }

    private static final long EXPIRATION_MS = 7 * 24 * 60 * 60 * 1000; // 7天

    private static SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    /** 生成 Token */
    public static String generateToken(Long userId, String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role);

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(getSigningKey())
                .compact();
    }

    /** 从 Token 中提取用户名 */
    public static String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    /** 从 Token 中提取过期时间 */
    public static Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    /** 提取指定 Claim */
    @SuppressWarnings("unchecked")
    public static <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private static Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /** 验证 Token 是否有效 */
    public static boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}
