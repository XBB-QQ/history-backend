package com.history.repository;

import com.history.entity.LearningProgressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LearningProgressRepository extends JpaRepository<LearningProgressEntity, Long> {

    List<LearningProgressEntity> findByUserId(String userId);

    List<LearningProgressEntity> findByUserIdAndResourceType(String userId, String resourceType);

    @Modifying
    @Query("UPDATE LearningProgressEntity l SET l.viewCount = l.viewCount + 1 WHERE l.userId = :userId AND l.resourceType = :type AND l.resourceId = :id")
    void incrementViewCount(@Param("userId") String userId, @Param("type") String resourceType, @Param("id") Long id);
}
