package com.history.controller;

import com.history.service.PageViewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 页面访问埋点 + 热度查询 API
 */
@RestController
@RequestMapping("/api/page-view")
@RequiredArgsConstructor
@Tag(name = "页面埋点", description = "前端页面访问埋点采集与热度查询")
public class PageViewController {

    private final PageViewService pageViewService;

    /**
     * 上报一次页面访问
     * POST /api/page-view
     * body: { pagePath, userId?, sessionId }
     */
    @PostMapping
    @Operation(summary = "上报页面访问", description = "前端在路由切换时调用，记录一次访问")
    public ResponseEntity<Map<String, Object>> record(@RequestBody Map<String, Object> body) {
        String pagePath = (String) body.get("pagePath");
        String userId = (String) body.get("userId");
        String sessionId = (String) body.get("sessionId");

        if (pagePath == null || pagePath.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "message", "pagePath required"));
        }
        if (sessionId == null || sessionId.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "message", "sessionId required"));
        }

        pageViewService.record(pagePath, userId, sessionId);
        return ResponseEntity.ok(Map.of("ok", true));
    }

    /**
     * 获取热度榜 Top N
     * GET /api/page-view/hot?limit=20&days=0
     * @param limit 返回条数（1-100）
     * @param days  时间窗口天数；0=全量（默认），7=近7天，30=近30天
     */
    @GetMapping("/hot")
    @Operation(summary = "热度榜", description = "返回访问次数最高的 N 个页面路径，支持时间窗口")
    public ResponseEntity<Map<String, Object>> hot(
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int days) {
        if (limit < 1) limit = 20;
        if (limit > 100) limit = 100;
        if (days < 0) days = 0;
        Map<String, Long> pages = pageViewService.getHotPages(limit, days);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("total", pages.size());
        result.put("days", days);
        result.put("pages", pages);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取冷门页面（访问次数最少的 N 个已访问页面）
     * GET /api/page-view/cold?limit=10
     */
    @GetMapping("/cold")
    @Operation(summary = "冷门页面预警", description = "返回访问次数最低的 N 个已访问页面，用于发现内容盲区")
    public ResponseEntity<Map<String, Object>> cold(@RequestParam(defaultValue = "10") int limit) {
        if (limit < 1) limit = 10;
        if (limit > 100) limit = 100;
        Map<String, Long> pages = pageViewService.getColdPages(limit);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("total", pages.size());
        result.put("pages", pages);
        return ResponseEntity.ok(result);
    }

    /**
     * 单个路径访问次数
     * GET /api/page-view/count?path=/timeline
     */
    @GetMapping("/count")
    @Operation(summary = "单页访问数", description = "返回指定路径的累计访问次数")
    public ResponseEntity<Map<String, Object>> count(@RequestParam String path) {
        long c = pageViewService.getCount(path);
        return ResponseEntity.ok(Map.of("path", path, "count", c));
    }
}
