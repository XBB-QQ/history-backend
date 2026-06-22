package com.history.util;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 简单的安全工具类
 * 生产环境建议使用 BCryptPasswordEncoder
 */
public class SecurityUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    /** 生成随机 salt */
    public static String generateSalt() {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /** SHA-256 哈希 */
    public static String sha256(String input, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes());
            byte[] hash = md.digest(input.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 hash failed", e);
        }
    }

    /** 验证密码 */
    public static boolean verifyPassword(String password, String salt, String expectedHash) {
        return sha256(password, salt).equals(expectedHash);
    }

    /** 生成 API Key */
    public static String generateApiKey() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return "hk_" + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
