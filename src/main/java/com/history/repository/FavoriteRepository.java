package com.history.repository;

import com.history.entity.FavoriteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<FavoriteEntity, Long> {
    List<FavoriteEntity> findByUserIdOrderByPinnedDescCreatedAtDesc(String userId);
    boolean existsByUserIdAndResourceId(String userId, Long resourceId);
    void deleteByUserIdAndResourceId(String userId, Long resourceId);
}
