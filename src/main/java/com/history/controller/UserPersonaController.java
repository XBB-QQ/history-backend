package com.history.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.history.service.UserPersonaService;
import com.history.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户 AI 画像 API
 * <p>
 * 跨设备同步前端 personaStore：
 * - GET    /api/users/me/persona  获取
 * - PUT    /api/users/me/persona  上传/更新（整体覆盖）
 * - DELETE /api/users/me/persona  清空
 * <p>
 * body 是整个 UserPersona JSON（前端 store 序列化结果）
 * <p>
 * 认证：手动校验 JWT（与 UserAuthController 风格一致，不依赖 SecurityContext）
 */
@RestController
@RequestMapping("/api/users/me/persona")
@RequiredArgsConstructor
@Tag(name = "用户画像", description = "AI 记忆中枢 — 用户画像跨设备同步")
public class UserPersonaController {

    private final UserPersonaService personaService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /** 从 Authorization header 提取用户名，校验失败返回 null */
    private String extractUsername(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        try {
            String token = authHeader.substring(7);
            return JwtUtil.extractUsername(token);
        } catch (Exception e) {
            return null;
        }
    }

    @GetMapping
    @Operation(summary = "获取当前用户的 AI 画像")
    public ResponseEntity<Map<String, Object>> get(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        String username = extractUsername(authHeader);
        if (username == null) {
            return ResponseEntity.status(401).body(Map.of("error", "未登录"));
        }
        // 用 LinkedHashMap 而非 Map.of，因为 Map.of 不允许 null 值
        java.util.LinkedHashMap<String, Object> body = new java.util.LinkedHashMap<>();
        body.put("username", username);
        body.put("found", false);
        personaService.get(username).ifPresent(json -> {
            body.put("persona", json);
            body.put("found", true);
        });
        return ResponseEntity.ok(body);
    }

    @PutMapping
    @Operation(summary = "上传/更新当前用户的 AI 画像", description = "整体覆盖，body 是 UserPersona JSON 字符串")
    public ResponseEntity<Map<String, Object>> save(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody Map<String, Object> body) {
        String username = extractUsername(authHeader);
        if (username == null) {
            return ResponseEntity.status(401).body(Map.of("error", "未登录"));
        }
        Object persona = body.get("persona");
        if (persona == null) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "message", "persona required"));
        }
        // persona 可能是对象或字符串，统一序列化为 JSON 字符串存
        String personaJson;
        try {
            personaJson = persona instanceof String
                ? (String) persona
                : objectMapper.writeValueAsString(persona);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "message", "persona 序列化失败: " + e.getMessage()));
        }
        personaService.save(username, personaJson);
        return ResponseEntity.ok(Map.of("ok", true, "username", username));
    }

    @DeleteMapping
    @Operation(summary = "清空当前用户的 AI 画像")
    public ResponseEntity<Map<String, Object>> clear(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        String username = extractUsername(authHeader);
        if (username == null) {
            return ResponseEntity.status(401).body(Map.of("error", "未登录"));
        }
        personaService.clear(username);
        return ResponseEntity.ok(Map.of("ok", true, "username", username));
    }
}
