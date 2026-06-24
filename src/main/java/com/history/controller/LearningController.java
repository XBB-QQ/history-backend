package com.history.controller;

import com.history.dto.LearningProgressDTO;
import com.history.dto.ReadingListDTO;
import com.history.service.LearningService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 学习进度与阅读清单 API
 */
@RestController
@RequestMapping("/api/user/learning")
@RequiredArgsConstructor
@Tag(name = "学习进度", description = "浏览记录和阅读清单")
public class LearningController {

    private final LearningService learningService;

    @PostMapping("/view")
    @Operation(summary = "记录浏览")
    public ResponseEntity<Void> recordView(
            @RequestBody Map<String, Object> body,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        if (userId == null) return ResponseEntity.status(401).build();
        String type = (String) body.get("resourceType");
        Long id = Long.valueOf(body.get("resourceId").toString());
        learningService.recordView(userId, type, id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/progress")
    @Operation(summary = "获取学习进度")
    public ResponseEntity<List<LearningProgressDTO>> getProgress(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        if (userId == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(learningService.getProgress(userId));
    }

    @GetMapping("/lists")
    @Operation(summary = "获取阅读清单列表")
    public ResponseEntity<List<ReadingListDTO>> getLists(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        if (userId == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(learningService.getLists(userId));
    }

    @PostMapping("/lists")
    @Operation(summary = "创建阅读清单")
    public ResponseEntity<ReadingListDTO> createList(
            @RequestBody Map<String, String> body,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        if (userId == null) return ResponseEntity.status(401).build();
        ReadingListDTO dto = learningService.createList(userId, body.get("name"), body.get("description"));
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/lists/{id}/resources")
    @Operation(summary = "向清单添加资源")
    public ResponseEntity<ReadingListDTO> addResource(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        if (userId == null) return ResponseEntity.status(401).build();
        ReadingListDTO dto = learningService.addResource(
            userId, id,
            (String) body.get("resourceType"),
            Long.valueOf(body.get("resourceId").toString()),
            (String) body.get("title")
        );
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/lists/{id}/resources/{resourceId}")
    @Operation(summary = "从清单移除资源")
    public ResponseEntity<Void> removeResource(
            @PathVariable Long id,
            @PathVariable Long resourceId,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        if (userId == null) return ResponseEntity.status(401).build();
        learningService.removeResource(userId, id, resourceId);
        return ResponseEntity.ok().build();
    }
}
