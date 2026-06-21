package com.history.controller;

import com.history.dto.PersonDTO;
import com.history.service.PersonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 人物 API 控制器
 */
@RestController
@RequestMapping("/api/v1/persons")
@RequiredArgsConstructor
@Tag(name = "人物管理", description = "历史人物 CRUD 与搜索接口")
public class PersonController {

    private final PersonService personService;

    @GetMapping
    @Operation(summary = "获取人物列表")
    public ResponseEntity<Page<PersonDTO>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String dynasty,
            @RequestParam(required = false) String role) {

        Pageable pageable = PageRequest.of(page, size);

        Page<PersonDTO> result;
        if (gender != null && !gender.isEmpty()) {
            result = personService.findByGender(gender, pageable);
        } else if (dynasty != null && !dynasty.isEmpty()) {
            result = personService.findByDynasty(dynasty, pageable);
        } else if (role != null && !role.isEmpty()) {
            result = personService.findByRole(role, pageable);
        } else {
            result = personService.findAll(pageable);
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取人物详情")
    public ResponseEntity<PersonDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(personService.findById(id));
    }

    @GetMapping("/uid/{uid}")
    @Operation(summary = "根据 UID 获取人物详情")
    public ResponseEntity<PersonDTO> getByUid(@PathVariable String uid) {
        return ResponseEntity.ok(personService.findByUid(uid));
    }

    @GetMapping("/search")
    @Operation(summary = "搜索人物")
    public ResponseEntity<Page<PersonDTO>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(personService.search(keyword, pageable));
    }
}
