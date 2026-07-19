package com.history.repository;

import com.history.entity.PageViewEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 页面访问记录 Repository
 */
@Repository
public interface PageViewRepository extends JpaRepository<PageViewEntity, Long> {

    /**
     * 热度榜（全量）：按路径聚合统计访问次数，返回 Top N
     * 返回数组：[pagePath, viewCount]
     */
    @Query("SELECT p.pagePath, COUNT(p) AS cnt FROM PageViewEntity p GROUP BY p.pagePath ORDER BY cnt DESC")
    List<Object[]> findTopHotPages(Pageable pageable);

    /**
     * 热度榜（时间窗口）：仅统计 since 之后的访问，返回 Top N
     */
    @Query("SELECT p.pagePath, COUNT(p) AS cnt FROM PageViewEntity p WHERE p.visitedAt >= :since GROUP BY p.pagePath ORDER BY cnt DESC")
    List<Object[]> findTopHotPagesSince(@Param("since") LocalDateTime since, Pageable pageable);

    /**
     * 冷门页面：按访问次数升序返回访问过的页面（访问最少的在前）
     */
    @Query("SELECT p.pagePath, COUNT(p) AS cnt FROM PageViewEntity p GROUP BY p.pagePath ORDER BY cnt ASC")
    List<Object[]> findColdPages(Pageable pageable);

    /**
     * 指定路径的总访问次数
     */
    long countByPagePath(@Param("pagePath") String pagePath);
}
