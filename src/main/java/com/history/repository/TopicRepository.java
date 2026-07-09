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

    @Query("SELECT DISTINCT t.category FROM TopicEntity t WHERE t.published = true")
    List<String> findDistinctCategories();

    @Query("SELECT t FROM TopicEntity t WHERE t.published = true AND " +
           "(LOWER(CAST(t.title AS string)) LIKE CONCAT('%', LOWER(:keyword), '%') OR " +
           "LOWER(CAST(t.summary AS string)) LIKE CONCAT('%', LOWER(:keyword), '%') OR " +
           "LOWER(CAST(t.description AS string)) LIKE CONCAT('%', LOWER(:keyword), '%'))")
    Page<TopicEntity> search(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT t FROM TopicEntity t WHERE t.published = true AND LOWER(t.tagsJson) LIKE CONCAT('%', LOWER(:tag), '%') ORDER BY t.sortOrder ASC")
    List<TopicEntity> findByTag(@Param("tag") String tag);
}
