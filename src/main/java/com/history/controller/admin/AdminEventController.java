package com.history.controller.admin;

import com.history.dto.EventDTO;
import com.history.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台管理 — 事件 CRUD
 */
@RestController
@RequestMapping("/api/admin/events")
@RequiredArgsConstructor
@Tag(name = "后台管理-事件", description = "事件管理接口")
public class AdminEventController {

    private final EventService eventService;

    @GetMapping
    @Operation(summary = "获取所有事件（管理）")
    public ResponseEntity<List<EventDTO>> list() {
        return ResponseEntity.ok(eventService.findAllOrdered());
    }

    @PostMapping
    @Operation(summary = "创建事件")
    public ResponseEntity<EventDTO> create(@RequestBody EventDTO dto) {
        return ResponseEntity.ok(eventService.createOrUpdate(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新事件")
    public ResponseEntity<EventDTO> update(@PathVariable Long id, @RequestBody EventDTO dto) {
        dto.setId(id);
        return ResponseEntity.ok(eventService.createOrUpdate(dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除事件")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        eventService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
