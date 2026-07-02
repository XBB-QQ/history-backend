package com.history.controller;

import com.history.dto.*;
import com.history.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 事件 API 控制器
 */
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Tag(name = "事件管理", description = "历史事件 CRUD 与搜索接口")
public class EventController {

    private final EventService eventService;

    /**
     * 获取事件列表（分页，按年份排序）
     */
    @GetMapping
    @Operation(summary = "获取事件列表", description = "支持分页、分类筛选、朝代筛选、年份范围筛选")
    @ApiResponse(description = "分页事件列表")
    public ResponseEntity<Page<EventDTO>> list(
            @Parameter(description = "页码，从 0 开始") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String dynasty,
            @RequestParam(required = false) Integer yearMin,
            @RequestParam(required = false) Integer yearMax) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "year"));

        Page<EventDTO> result;
        if (category != null && !category.isEmpty()) {
            result = eventService.findByCategory(category, pageable);
        } else if (dynasty != null && !dynasty.isEmpty()) {
            result = eventService.findByDynasty(dynasty, pageable);
        } else if (yearMin != null && yearMax != null) {
            result = eventService.findByYearRange(yearMin, yearMax, pageable);
        } else {
            result = eventService.findAll(pageable);
        }

        return ResponseEntity.ok(result);
    }

    /**
     * 获取时间轴事件（不分页，按年份升序）
     */
    @GetMapping("/timeline")
    @Operation(summary = "获取时间轴事件", description = "返回所有事件，按年份升序排列，用于时间轴展示")
    public ResponseEntity<List<EventDTO>> timeline() {
        return ResponseEntity.ok(eventService.findAllOrdered());
    }

    /**
     * 根据 ID 获取事件详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取事件详情")
    public ResponseEntity<EventDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.findById(id));
    }

    /**
     * 根据 UID 获取事件详情
     */
    @GetMapping("/uid/{uid}")
    @Operation(summary = "根据 UID 获取事件详情")
    public ResponseEntity<EventDTO> getByUid(@PathVariable String uid) {
        return ResponseEntity.ok(eventService.findByUid(uid));
    }

    /**
     * 搜索事件
     */
    @GetMapping("/search")
    @Operation(summary = "搜索事件")
    public ResponseEntity<Page<EventDTO>> search(
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(eventService.search(keyword, pageable));
    }

    /**
     * 获取事件关联数据（人物 + 知识卡片）
     */
    @GetMapping("/{id}/related")
    @Operation(summary = "获取事件关联数据")
    public ResponseEntity<EventRelatedDTO> getRelated(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getRelatedData(id));
    }
}
