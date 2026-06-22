package com.history.controller.admin;

import com.history.dto.PersonDTO;
import com.history.service.PersonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台管理 — 人物 CRUD
 */
@RestController
@RequestMapping("/api/admin/persons")
@RequiredArgsConstructor
@Tag(name = "后台管理-人物", description = "人物管理接口")
public class AdminPersonController {

    private final PersonService personService;

    @GetMapping
    @Operation(summary = "获取所有人物（管理）")
    public ResponseEntity<List<PersonDTO>> list() {
        return ResponseEntity.ok(personService.findAllOrdered());
    }

    @PostMapping
    @Operation(summary = "创建人物")
    public ResponseEntity<PersonDTO> create(@RequestBody PersonDTO dto) {
        return ResponseEntity.ok(personService.createOrUpdate(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新人物")
    public ResponseEntity<PersonDTO> update(@PathVariable Long id, @RequestBody PersonDTO dto) {
        dto.setId(id);
        return ResponseEntity.ok(personService.createOrUpdate(dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除人物")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        personService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
