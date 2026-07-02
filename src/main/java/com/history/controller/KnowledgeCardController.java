package com.history.controller;

import com.history.dto.KnowledgeCardDTO;
import com.history.service.KnowledgeCardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 知识卡片 API 控制器
 */
@RestController
@RequestMapping("/api/knowledge")
@RequiredArgsConstructor
@Tag(name = "知识卡片", description = "知识卡片查询接口")
public class KnowledgeCardController {

    private final KnowledgeCardService knowledgeCardService;

    @GetMapping
    @Operation(summary = "获取知识卡片列表")
    public ResponseEntity<Page<KnowledgeCardDTO>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(knowledgeCardService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取知识卡片详情")
    public ResponseEntity<KnowledgeCardDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(knowledgeCardService.findById(id));
    }

    @GetMapping("/uid/{uid}")
    @Operation(summary = "根据 UID 获取知识卡片详情")
    public ResponseEntity<KnowledgeCardDTO> getByUid(@PathVariable String uid) {
        return ResponseEntity.ok(knowledgeCardService.findByUid(uid));
    }

    @GetMapping("/search")
    @Operation(summary = "搜索知识卡片")
    public ResponseEntity<Page<KnowledgeCardDTO>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(knowledgeCardService.search(keyword, pageable));
    }

    /**
     * 获取标签统计（用于标签云）
     */
    @GetMapping("/tags")
    @Operation(summary = "获取标签统计")
    public ResponseEntity<List<Map<String, Object>>> getTagStats() {
        List<Map<String, Object>> stats = knowledgeCardService.getTagStatistics();
        return ResponseEntity.ok(stats);
    }
}
