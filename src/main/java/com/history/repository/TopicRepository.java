package com.history.repository;

import com.history.entity.TopicEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 专题数据访问层
 */
@Repository
public interface TopicRepository extends JpaRepository<TopicEntity, Long> {

    Optional<TopicEntity> findByUid(String uid);

    Page<TopicEntity> findByPublishedTrue(Pageable pageable);

    Page<TopicEntity> findByCategoryAndPublishedTrue(String category, Pageable pageable);

    List<TopicEntity> findByCategoryAndPublishedTrueOrderBySortOrderAsc(String category);

    @Query("SELECT DISTINCT t.category FROM TopicEntity WHERE published = true")
    List<String> findDistinctCategories();

    @Query("SELECT t FROM TopicEntity t WHERE t.published = true AND " +
           "(LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(t.summary) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<TopicEntity> search(@Param("keyword") String keyword, Pageable pageable);
}
