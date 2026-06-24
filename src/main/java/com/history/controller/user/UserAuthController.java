package com.history.controller.user;

import com.history.dto.*;
import com.history.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户认证控制器
 * POST /api/auth/register — 注册
 * POST /api/auth/login — 登录
 * POST /api/auth/verify — 验证 Token
 * GET  /api/auth/me — 获取当前用户信息
 * PUT  /api/auth/me — 更新用户信息
 */
@RestController
@RequestMapping("/api/auth")
public class UserAuthController {

    private final UserService userService;

    public UserAuthController(UserService userService) {
        this.userService = userService;
    }

    /** 注册 */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            UserDTO user = userService.register(request);
            Map<String, Object> body = new HashMap<>();
            body.put("user", user);
            body.put("message", "注册成功");
            return ResponseEntity.ok(body);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** 登录 */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            String token = userService.login(request);
            UserDTO user = userService.getUserByUsername(request.getUsername());
            Map<String, Object> body = new HashMap<>();
            body.put("token", token);
            body.put("user", user);
            body.put("message", "登录成功");
            return ResponseEntity.ok(body);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** 验证 Token */
    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("error", "无效的 Token"));
        }
        String token = authHeader.substring(7);

        // 简单验证：尝试解析 Token
        try {
            String username = com.history.util.JwtUtil.extractUsername(token);
            UserDTO user = userService.getUserByUsername(username);
            return ResponseEntity.ok(Map.of("valid", true, "user", user));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("valid", false, "error", "Token 无效或已过期"));
        }
    }

    /** 获取当前用户信息 */
    @GetMapping("/me")
    public ResponseEntity<?> getMe(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("error", "未登录"));
        }
        String token = authHeader.substring(7);
        String username = com.history.util.JwtUtil.extractUsername(token);
        UserDTO user = userService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }

    /** 更新当前用户信息 */
    @PutMapping("/me")
    public ResponseEntity<?> updateMe(@RequestHeader("Authorization") String authHeader,
                                      @RequestBody UserDTO dto) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("error", "未登录"));
        }
        String token = authHeader.substring(7);
        String username = com.history.util.JwtUtil.extractUsername(token);

        UserDTO updated = userService.updateUser(username, dto);
        return ResponseEntity.ok(updated);
    }
}
