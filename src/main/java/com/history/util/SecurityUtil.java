package com.history.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 安全工具类
 */
public class SecurityUtil {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    /** 生成随机 salt（保留兼容性，BCrypt 内部已处理） */
    public static String generateSalt() {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /** BCrypt 加密密码（推荐，替代 SHA-256） */
    public static String encodePassword(String password) {
        return PASSWORD_ENCODER.encode(password);
    }

    /** BCrypt 验证密码 */
    public static boolean verifyPassword(String password, String encodedHash) {
        return PASSWORD_ENCODER.matches(password, encodedHash);
    }

    /** 兼容旧 SHA-256 + Salt 密码（迁移用） */
    public static String sha256(String input, String salt) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes());
            byte[] hash = md.digest(input.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 hash failed", e);
        }
    }

    /** 生成 API Key */
    public static String generateApiKey() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return "hk_" + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
