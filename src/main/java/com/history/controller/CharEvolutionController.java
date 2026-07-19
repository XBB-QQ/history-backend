package com.history.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.history.dto.CharEvolutionResponse;
import com.history.service.HanziyuanFetcher;
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
 * 数据源：
 *   1. classpath:data/char-evolutions.json（内置 30 个常用字，手工绘制简化 SVG path）
 *   2. hanziyuan.net 实时抓取（任意汉字，真实字源 SVG，24h 缓存）
 */
@Slf4j
@RestController
@RequestMapping("/api/char-evolution")
@RequiredArgsConstructor
@Tag(name = "汉字演变", description = "汉字字形演变查询")
public class CharEvolutionController {

    private final ObjectMapper objectMapper;
    private final HanziyuanFetcher hanziyuanFetcher;

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

    /** 返回单个汉字的完整演变数据
     *  优先内置数据；未命中则实时抓取 hanziyuan.net
     */
    @GetMapping("/{char}")
    @Operation(summary = "查询单个汉字演变",
            description = "返回指定汉字从甲骨文到楷书的 5 阶段演变数据。内置 30 字直接返回；其他字实时抓取 hanziyuan.net（24h 缓存）")
    public ResponseEntity<?> get(@PathVariable("char") String character) {
        // 1. 命中内置数据
        CharEvolutionResponse response = store.get(character);
        if (response != null) {
            return ResponseEntity.ok(response);
        }

        // 2. 调用 hanziyuan fetcher
        log.info("内置数据未命中「{}」，尝试 hanziyuan.net 抓取", character);
        CharEvolutionResponse fetched = hanziyuanFetcher.fetch(character);
        if (fetched != null) {
            return ResponseEntity.ok(fetched);
        }

        // 3. 都失败：返回 404 + 友好提示
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "error", "字源数据未收录，且 hanziyuan.net 抓取失败",
                "char", character,
                "hint", "可访问 https://hanziyuan.net/#" + character + " 查看",
                "available", store.keySet()
        ));
    }
}
