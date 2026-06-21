package com.history.controller;

import com.history.dto.DynastyDTO;
import com.history.service.DynastyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 朝代 API 控制器
 */
@RestController
@RequestMapping("/api/v1/dynasties")
@RequiredArgsConstructor
@Tag(name = "朝代管理", description = "朝代查询接口")
public class DynastyController {

    private final DynastyService dynastyService;

    @GetMapping
    @Operation(summary = "获取朝代列表")
    public ResponseEntity<Page<DynastyDTO>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(dynastyService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取朝代详情")
    public ResponseEntity<DynastyDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(dynastyService.findById(id));
    }

    @GetMapping("/uid/{uid}")
    @Operation(summary = "根据 UID 获取朝代详情")
    public ResponseEntity<DynastyDTO> getByUid(@PathVariable String uid) {
        return ResponseEntity.ok(dynastyService.findByUid(uid));
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "根据名称获取朝代详情")
    public ResponseEntity<DynastyDTO> getByName(@PathVariable String name) {
        return ResponseEntity.ok(dynastyService.findByName(name));
    }
}
