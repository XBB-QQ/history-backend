package com.history.service;

import com.history.entity.PageViewEntity;
import com.history.repository.PageViewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 页面访问记录服务
 * - record() 异步落库，不阻塞业务请求
 * - getHotPages() 同步查询热度榜（支持时间窗口）
 * - getColdPages() 查询访问最少的页面（冷门预警）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PageViewService {

    private final PageViewRepository pageViewRepository;

    /**
     * 异步记录一次页面访问
     */
    @Async
    @Transactional
    public void record(String pagePath, String userId, String sessionId) {
        try {
            PageViewEntity entity = new PageViewEntity();
            entity.setPagePath(pagePath);
            entity.setUserId(userId);
            entity.setSessionId(sessionId != null ? sessionId : "anonymous");
            pageViewRepository.save(entity);
        } catch (Exception e) {
            // 埋点失败不影响业务，仅记录日志
            log.warn("页面访问记录失败 path={} sid={} err={}", pagePath, sessionId, e.getMessage());
        }
    }

    /**
     * 获取热度榜 Top N（全量）
     * 返回有序 Map：pagePath -> viewCount（按访问数倒序）
     */
    public Map<String, Long> getHotPages(int limit) {
        return getHotPages(limit, 0);
    }

    /**
     * 获取热度榜 Top N（带时间窗口）
     * @param limit  返回条数
     * @param days   时间窗口天数；0 表示不限时间（全量）
     */
    public Map<String, Long> getHotPages(int limit, int days) {
        List<Object[]> rows;
        if (days <= 0) {
            rows = pageViewRepository.findTopHotPages(PageRequest.of(0, limit));
        } else {
            LocalDateTime since = LocalDateTime.now().minusDays(days);
            rows = pageViewRepository.findTopHotPagesSince(since, PageRequest.of(0, limit));
        }
        Map<String, Long> result = new LinkedHashMap<>();
        for (Object[] row : rows) {
            String path = (String) row[0];
            Long count = (Long) row[1];
            result.put(path, count);
        }
        return result;
    }

    /**
     * 获取冷门页面（访问次数最少的 N 个已访问页面）
     * 返回有序 Map：pagePath -> viewCount（按访问数升序）
     */
    public Map<String, Long> getColdPages(int limit) {
        List<Object[]> rows = pageViewRepository.findColdPages(PageRequest.of(0, limit));
        Map<String, Long> result = new LinkedHashMap<>();
        for (Object[] row : rows) {
            String path = (String) row[0];
            Long count = (Long) row[1];
            result.put(path, count);
        }
        return result;
    }

    /**
     * 单个路径访问次数
     */
    public long getCount(String pagePath) {
        return pageViewRepository.countByPagePath(pagePath);
    }
}
