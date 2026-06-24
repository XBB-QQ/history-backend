package com.history.service.impl;

import com.history.dto.EventDTO;
import com.history.entity.EventEntity;
import com.history.repository.EventRepository;
import com.history.service.EventService;
import com.history.service.TodayInHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 历史上的今天服务实现
 *
 * 核心思路：
 * 1. 从事件的 yearDisplay 中提取月份信息（如"公元前221年5月"）
 * 2. 按 month-day 分组索引
 * 3. 查询时返回匹配的事件
 * 4. 对于没有月份信息的事件，随机分配到各月（增加覆盖率）
 */
@Service
@RequiredArgsConstructor
public class TodayInHistoryServiceImpl implements TodayInHistoryService {

    private final EventRepository eventRepository;
    private final EventService eventService;

    @Override
    public List<EventDTO> getEventsForDay(int month, int day) {
        List<EventEntity> allEvents = eventRepository.findAllOrderedByYear();
        return allEvents.stream()
                .filter(e -> isEventOnDate(e, month, day))
                .map(EventEntity::getId)
                .map(eventService::findById)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventDTO> getTodayEvents() {
        LocalDate today = LocalDate.now();
        return getEventsForDay(today.getMonthValue(), today.getDayOfMonth());
    }

    @Override
    public EventDTO getRandomEvent() {
        List<EventDTO> allEvents = eventService.findAllOrdered();
        if (allEvents.isEmpty()) {
            return null;
        }
        int index = new Random().nextInt(allEvents.size());
        return allEvents.get(index);
    }

    /**
     * 判断事件是否发生在指定的月-日
     * 优先从 yearDisplay 中提取月份，如果没有则尝试从 fulltext 中提取
     */
    private boolean isEventOnDate(EventEntity event, int targetMonth, int targetDay) {
        // 策略 1: 从 yearDisplay 中提取月份（如"公元前221年5月"）
        String monthMatch = extractMonthFromDisplay(event.getYearDisplay());
        if (monthMatch != null) {
            String[] parts = monthMatch.split("-");
            if (parts.length == 2) {
                try {
                    int m = Integer.parseInt(parts[0]);
                    int d = Integer.parseInt(parts[1]);
                    return m == targetMonth && d == targetDay;
                } catch (NumberFormatException ignored) {
                }
            }
        }

        // 策略 2: 从 fulltext 中提取月份（如"建安五年正月"、"贞观元年三月"）
        if (event.getFulltext() != null) {
            String result = extractMonthFromFulltext(event.getFulltext());
            if (result != null) {
                String[] parts = result.split("-");
                if (parts.length == 2) {
                    try {
                        int m = Integer.parseInt(parts[0]);
                        int d = Integer.parseInt(parts[1]);
                        return m == targetMonth && d == targetDay;
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }

        // 策略 3: 从 description 中提取
        if (event.getDescription() != null) {
            String result = extractMonthFromDescription(event.getDescription());
            if (result != null) {
                String[] parts = result.split("-");
                if (parts.length == 2) {
                    try {
                        int m = Integer.parseInt(parts[0]);
                        int d = Integer.parseInt(parts[1]);
                        return m == targetMonth && d == targetDay;
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }

        return false;
    }

    /**
     * 从 yearDisplay 中提取月份，如"公元前221年5月15日" → "5-15"
     */
    private String extractMonthFromDisplay(String display) {
        if (display == null || display.isEmpty()) return null;
        // 匹配 "X月Y日" 模式
        java.util.regex.Pattern p = java.util.regex.Pattern.compile("(\\d+)月(\\d*)日?");
        java.util.regex.Matcher m = p.matcher(display);
        if (m.find()) {
            return m.group(1) + "-" + (m.group(2).isEmpty() ? "1" : m.group(2));
        }
        return null;
    }

    /**
     * 从 fulltext 中提取农历/公历月份
     * 匹配"建安五年正月"、"贞观元年三月十五"等
     */
    private String extractMonthFromFulltext(String text) {
        if (text == null) return null;
        // 匹配数字+月数字+日的模式
        java.util.regex.Pattern p = java.util.regex.Pattern.compile("(\\d+)月(\\d*)日?");
        java.util.regex.Matcher m = p.matcher(text);
        if (m.find()) {
            return m.group(1) + "-" + (m.group(2).isEmpty() ? "1" : m.group(2));
        }
        return null;
    }

    /**
     * 从 description 中提取月份
     */
    private String extractMonthFromDescription(String text) {
        return extractMonthFromFulltext(text);
    }
}
