package com.history.controller;

import com.history.dto.TopicDTO;
import com.history.service.TopicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 专题 API 控制器
 */
@RestController
@RequestMapping("/api/v1/topics")
@RequiredArgsConstructor
@Tag(name = "专题", description = "专题深度长文接口")
public class TopicController {

    private final TopicService topicService;

    @GetMapping
    @Operation(summary = "获取专题列表")
    public ResponseEntity<Page<TopicDTO>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(topicService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取专题详情")
    public ResponseEntity<TopicDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(topicService.findById(id));
    }

    @GetMapping("/uid/{uid}")
    @Operation(summary = "根据 UID 获取专题详情")
    public ResponseEntity<TopicDTO> getByUid(@PathVariable String uid) {
        return ResponseEntity.ok(topicService.findByUid(uid));
    }

    @GetMapping("/search")
    @Operation(summary = "搜索专题")
    public ResponseEntity<Page<TopicDTO>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(topicService.search(keyword, pageable));
    }

    @GetMapping("/categories")
    @Operation(summary = "获取所有分类")
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.ok(topicService.getCategories());
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "按分类获取专题")
    public ResponseEntity<List<TopicDTO>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(topicService.findByCategory(category));
    }
}
