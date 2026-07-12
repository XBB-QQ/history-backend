package com.history.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.history.dto.CharEvolutionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 汉字演变 Controller
 * 提供汉字从甲骨文到楷书的字形演变数据
 * 数据源：classpath:data/char-evolutions.json（内置 30 个常用字）
 */
@Slf4j
@RestController
@RequestMapping("/api/char-evolution")
@RequiredArgsConstructor
@Tag(name = "汉字演变", description = "汉字字形演变查询")
public class CharEvolutionController {

    private final ObjectMapper objectMapper;

    /** 启动时加载的汉字数据，key=汉字字符 */
    private final Map<String, CharEvolutionResponse> store = new ConcurrentHashMap<>();

    @PostConstruct
    void load() throws IOException {
        try (InputStream is = new ClassPathResource("data/char-evolutions.json").getInputStream()) {
            List<CharEvolutionResponse> list = objectMapper.readValue(is, new TypeReference<>() {});
            for (CharEvolutionResponse e : list) {
                store.put(e.getCharacter(), e);
            }
            log.info("加载汉字演变数据 {} 条", store.size());
        }
    }

    /** 返回所有可用汉字列表（仅字符，供前端渲染选择按钮） */
    @GetMapping
    @Operation(summary = "获取所有可用汉字列表", description = "返回内置的所有汉字字符及释义，供前端渲染选择按钮")
    public ResponseEntity<List<CharEvolutionResponse>> list() {
        return ResponseEntity.ok(new ArrayList<>(store.values()));
    }

    /** 返回单个汉字的完整演变数据 */
    @GetMapping("/{char}")
    @Operation(summary = "查询单个汉字演变", description = "返回指定汉字从甲骨文到楷书的 5 阶段演变数据")
    public ResponseEntity<?> get(@PathVariable("char") String character) {
        CharEvolutionResponse response = store.get(character);
        if (response == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "error", "未收录该汉字",
                    "char", character,
                    "available", store.keySet()
            ));
        }
        return ResponseEntity.ok(response);
    }
}
