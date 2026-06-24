package com.history.repository;

import com.history.entity.ReadingListEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReadingListRepository extends JpaRepository<ReadingListEntity, Long> {
    List<ReadingListEntity> findByUserId(String userId);
}
