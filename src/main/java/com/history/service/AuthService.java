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
     * 初始化默认管理员账号
     */
    @PostConstruct
    public void initAdmin() {
        if (adminUserRepository.count() == 0) {
            String salt = SecurityUtil.generateSalt();
            String passwordHash = SecurityUtil.sha256("admin123", salt);
            String apiKey = SecurityUtil.generateApiKey();

            AdminUserEntity admin = AdminUserEntity.builder()
                .username("admin")
                .passwordHash(passwordHash)
                .salt(salt)
                .role("admin")
                .apiKey(apiKey)
                .build();
            adminUserRepository.save(admin);
            log.info("初始化默认管理员账号: admin / admin123");
        }
    }

    /**
     * 登录
     */
    public AuthResponse login(AuthRequest request) {
        AdminUserEntity user = adminUserRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new RuntimeException("用户名或密码错误"));

        if (!SecurityUtil.verifyPassword(request.getPassword(), user.getSalt(), user.getPasswordHash())) {
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
        String salt = SecurityUtil.generateSalt();
        String passwordHash = SecurityUtil.sha256(password, salt);
        String apiKey = SecurityUtil.generateApiKey();

        AdminUserEntity user = AdminUserEntity.builder()
            .username(username)
            .passwordHash(passwordHash)
            .salt(salt)
            .role(role)
            .apiKey(apiKey)
            .build();
        return adminUserRepository.save(user);
    }
}
