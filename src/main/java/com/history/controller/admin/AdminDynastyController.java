package com.history.controller.admin;

import com.history.dto.DynastyDTO;
import com.history.service.DynastyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台管理 — 朝代 CRUD
 */
@RestController
@RequestMapping("/api/admin/dynasties")
@RequiredArgsConstructor
@Tag(name = "后台管理-朝代", description = "朝代管理接口")
public class AdminDynastyController {

    private final DynastyService dynastyService;

    @GetMapping
    @Operation(summary = "获取所有朝代（管理）")
    public ResponseEntity<List<DynastyDTO>> list() {
        return ResponseEntity.ok(dynastyService.findAllOrdered());
    }

    @PostMapping
    @Operation(summary = "创建朝代")
    public ResponseEntity<DynastyDTO> create(@RequestBody DynastyDTO dto) {
        return ResponseEntity.ok(dynastyService.createOrUpdate(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新朝代")
    public ResponseEntity<DynastyDTO> update(@PathVariable Long id, @RequestBody DynastyDTO dto) {
        dto.setId(id);
        return ResponseEntity.ok(dynastyService.createOrUpdate(dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除朝代")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        dynastyService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
