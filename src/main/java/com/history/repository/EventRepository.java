package com.history.repository;

import com.history.entity.EventEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long> {

    Optional<EventEntity> findByUid(String uid);

    Page<EventEntity> findByCategory(String category, Pageable pageable);

    Page<EventEntity> findByDynasty_Name(String dynastyName, Pageable pageable);

    Page<EventEntity> findByYearBetween(Integer start, Integer end, Pageable pageable);

    @Query("SELECT e FROM EventEntity e WHERE " +
           "LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.fulltext) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<EventEntity> search(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT e FROM EventEntity e ORDER BY e.year ASC")
    List<EventEntity> findAllOrderedByYear();

    @Query("SELECT e FROM EventEntity e ORDER BY e.year ASC")
    Page<EventEntity> findAllOrderedByYear(Pageable pageable);
}
