package com.history.controller.admin;

import com.history.dto.TopicDTO;
import com.history.service.TopicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 专题管理后台控制器
 */
@RestController
@RequestMapping("/api/admin/topics")
@RequiredArgsConstructor
@Tag(name = "管理后台-专题", description = "专题 CRUD 管理接口")
public class AdminTopicController {

    private final TopicService topicService;

    @PostMapping
    @Operation(summary = "创建/更新专题")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TopicDTO> createOrUpdate(@RequestBody TopicDTO dto) {
        return ResponseEntity.ok(topicService.createOrUpdate(dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除专题")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        topicService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
