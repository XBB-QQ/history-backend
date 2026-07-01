package com.history.service;

import com.history.dto.AuthRequest;
import com.history.dto.AuthResponse;
import com.history.entity.AdminUserEntity;
import com.history.repository.AdminUserRepository;
import com.history.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;

/**
 * 认证服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AdminUserRepository adminUserRepository;

    /**
     * 初始化默认管理员账号（仅当数据库为空时执行一次）
     * 密码通过环境变量 ADMIN_DEFAULT_PASSWORD 设置，默认随机生成
     */
    @PostConstruct
    public void initAdmin() {
        if (adminUserRepository.count() == 0) {
            String password = System.getenv("ADMIN_DEFAULT_PASSWORD");
            if (password == null || password.isBlank()) {
                // 无环境变量时生成随机密码并记录到日志
                password = SecurityUtil.generateApiKey().substring(3); // 去掉 "hk_" 前缀
                log.warn("未设置 ADMIN_DEFAULT_PASSWORD，已生成随机管理员密码，请记录: {}", password);
            }
            String passwordHash = SecurityUtil.encodePassword(password);
            String apiKey = SecurityUtil.generateApiKey();

            AdminUserEntity admin = AdminUserEntity.builder()
                .username("admin")
                .passwordHash(passwordHash)
                .salt("")  // BCrypt 不需要外部 salt
                .role("admin")
                .apiKey(apiKey)
                .build();
            adminUserRepository.save(admin);
            log.info("初始化默认管理员账号: admin (密码已通过环境变量或日志获取)");
        }
    }

    /**
     * 登录
     */
    public AuthResponse login(AuthRequest request) {
        AdminUserEntity user = adminUserRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new RuntimeException("用户名或密码错误"));

        // 优先尝试 BCrypt 验证
        boolean valid = SecurityUtil.verifyPassword(request.getPassword(), user.getPasswordHash());

        // 如果 BCrypt 失败且 salt 不为空，尝试旧 SHA-256 + Salt 兼容验证（迁移用）
        if (!valid && user.getSalt() != null && !user.getSalt().isEmpty()) {
            String hash = SecurityUtil.sha256(request.getPassword(), user.getSalt());
            valid = hash.equals(user.getPasswordHash());
        }

        if (!valid) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 更新最后登录信息
        user.setLastLoginAt(java.time.LocalDateTime.now());
        adminUserRepository.save(user);

        return new AuthResponse(
            user.getId(),
            user.getUsername(),
            user.getRole(),
            user.getApiKey(),
            "登录成功"
        );
    }

    /**
     * 通过 API Key 验证
     */
    public AdminUserEntity validateApiKey(String apiKey) {
        return adminUserRepository.findByApiKey(apiKey)
            .orElseThrow(() -> new RuntimeException("无效的 API Key"));
    }

    /**
     * 创建新用户
     */
    @Transactional
    public AdminUserEntity createUser(String username, String password, String role) {
        if (adminUserRepository.existsByUsername(username)) {
            throw new RuntimeException("用户名已存在");
        }
        String passwordHash = SecurityUtil.encodePassword(password);

        AdminUserEntity user = AdminUserEntity.builder()
            .username(username)
            .passwordHash(passwordHash)
            .salt("")
            .role(role)
            .apiKey(SecurityUtil.generateApiKey())
            .build();
        return adminUserRepository.save(user);
    }
}
