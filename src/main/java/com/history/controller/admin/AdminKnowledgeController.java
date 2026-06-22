package com.history.controller.admin;

import com.history.dto.KnowledgeCardDTO;
import com.history.service.KnowledgeCardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台管理 — 知识卡片 CRUD
 */
@RestController
@RequestMapping("/api/admin/knowledge")
@RequiredArgsConstructor
@Tag(name = "后台管理-知识卡片", description = "知识卡片管理接口")
public class AdminKnowledgeController {

    private final KnowledgeCardService knowledgeCardService;

    @GetMapping
    @Operation(summary = "获取所有知识卡片（管理）")
    public ResponseEntity<List<KnowledgeCardDTO>> list() {
        return ResponseEntity.ok(knowledgeCardService.findAllOrdered());
    }

    @PostMapping
    @Operation(summary = "创建知识卡片")
    public ResponseEntity<KnowledgeCardDTO> create(@RequestBody KnowledgeCardDTO dto) {
        return ResponseEntity.ok(knowledgeCardService.createOrUpdate(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新知识卡片")
    public ResponseEntity<KnowledgeCardDTO> update(@PathVariable Long id, @RequestBody KnowledgeCardDTO dto) {
        dto.setId(id);
        return ResponseEntity.ok(knowledgeCardService.createOrUpdate(dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除知识卡片")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        knowledgeCardService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
