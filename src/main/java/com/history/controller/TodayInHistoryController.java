package com.history.controller;

import com.history.dto.EventDTO;
import com.history.service.TodayInHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 历史上的今天 API 控制器
 */
@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
@Tag(name = "公共接口", description = "历史上的今天、每日推荐等公开接口")
public class TodayInHistoryController {

    private final TodayInHistoryService todayService;

    /**
     * 获取今天发生的历史事件
     */
    @GetMapping("/today")
    @Operation(summary = "历史上的今天", description = "返回今天日期对应发生的历史事件列表")
    public ResponseEntity<List<EventDTO>> getToday() {
        List<EventDTO> events = todayService.getTodayEvents();
        return ResponseEntity.ok(events);
    }

    /**
     * 获取每日推荐事件
     */
    @GetMapping("/daily-recommend")
    @Operation(summary = "每日推荐", description = "返回一个随机推荐的历史事件")
    public ResponseEntity<Map<String, Object>> getDailyRecommend() {
        EventDTO event = todayService.getRandomEvent();
        if (event == null) {
            return ResponseEntity.ok(Map.of(
                    "found", false,
                    "message", "暂无数据"
            ));
        }
        return ResponseEntity.ok(Map.of(
                "found", true,
                "event", event
        ));
    }
}
