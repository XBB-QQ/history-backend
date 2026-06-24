package com.history.controller;

import com.history.dto.FavoriteDTO;
import com.history.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 收藏 API 控制器（支持 JWT 认证）
 */
@RestController
@RequestMapping("/api/v1/favorites")
@RequiredArgsConstructor
@Tag(name = "收藏管理", description = "用户收藏 CRUD 接口")
public class FavoriteController {

    private final FavoriteService favoriteService;

    /** 从 SecurityContext 获取当前用户名 */
    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName() != null) {
            return auth.getName();
        }
        return "anonymous";
    }

    @GetMapping
    @Operation(summary = "获取收藏列表")
    public ResponseEntity<List<FavoriteDTO>> getFavorites() {
        return ResponseEntity.ok(favoriteService.getFavorites(getCurrentUserId()));
    }

    @PostMapping
    @Operation(summary = "添加收藏")
    public ResponseEntity<FavoriteDTO> addFavorite(@RequestBody Map<String, Object> body) {
        String resourceType = (String) body.get("resourceType");
        Long resourceId = Long.valueOf(String.valueOf(body.get("resourceId")));
        String title = (String) body.get("title");
        return ResponseEntity.ok(favoriteService.addFavorite(getCurrentUserId(), resourceType, resourceId, title));
    }

    @DeleteMapping("/{resourceId}")
    @Operation(summary = "移除收藏")
    public ResponseEntity<Void> removeFavorite(@PathVariable Long resourceId) {
        favoriteService.removeFavorite(getCurrentUserId(), resourceId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{resourceId}/pin")
    @Operation(summary = "切换置顶")
    public ResponseEntity<FavoriteDTO> togglePin(@PathVariable Long resourceId) {
        favoriteService.togglePin(getCurrentUserId(), resourceId);
        return ResponseEntity.ok(favoriteService.getFavorites(getCurrentUserId()).stream()
            .filter(f -> f.getResourceId().equals(resourceId))
            .findFirst()
            .orElse(null));
    }
}
