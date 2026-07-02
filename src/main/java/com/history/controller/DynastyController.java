package com.history.controller;

import com.history.dto.*;
import com.history.service.DynastyService;
import com.history.service.EventService;
import com.history.service.PersonService;
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

/**
 * 朝代 API 控制器
 */
@RestController
@RequestMapping("/api/dynasties")
@RequiredArgsConstructor
@Tag(name = "朝代管理", description = "朝代查询接口")
public class DynastyController {

    private final DynastyService dynastyService;
    private final EventService eventService;
    private final PersonService personService;
    private final KnowledgeCardService knowledgeCardService;

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

    /**
     * 获取朝代详情（含关联事件、人物、知识卡片）
     */
    @GetMapping("/{id}/details")
    @Operation(summary = "获取朝代详情及关联数据")
    public ResponseEntity<DynastyDetailsDTO> getDetails(@PathVariable Long id) {
        DynastyDTO dynastyDTO = dynastyService.findById(id);
        List<EventDTO> events = eventService.findByDynasty(dynastyDTO.getName(), PageRequest.of(0, 100)).getContent();
        List<PersonDTO> persons = personService.findByDynasty(dynastyDTO.getName(), PageRequest.of(0, 100)).getContent();
        // 知识卡片通过 dynastyName 匹配（简化处理）
        List<KnowledgeCardDTO> knowledgeCards = knowledgeCardService.findAll(PageRequest.of(0, 100)).getContent().stream()
                .filter(k -> k.getDynastyName() != null && k.getDynastyName().equals(dynastyDTO.getName()))
                .toList();

        return ResponseEntity.ok(new DynastyDetailsDTO(dynastyDTO, events, persons, knowledgeCards));
    }
}
