package com.history.controller.admin;

import com.history.dto.AuthRequest;
import com.history.dto.AuthResponse;
import com.history.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 后台管理 — 认证接口
 */
@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
@Tag(name = "后台管理-认证", description = "管理员登录接口")
public class AdminAuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "管理员登录")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new AuthResponse(null, null, null, null, e.getMessage()));
        }
    }

    @PostMapping("/verify")
    @Operation(summary = "验证 API Key")
    public ResponseEntity<Map<String, Object>> verifyApiKey(@RequestBody Map<String, String> body) {
        try {
            authService.validateApiKey(body.get("apiKey"));
            return ResponseEntity.ok(Map.of("valid", true, "message", "验证通过"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("valid", false, "message", e.getMessage()));
        }
    }
}
