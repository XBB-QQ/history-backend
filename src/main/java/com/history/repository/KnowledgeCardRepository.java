package com.history.repository;

import com.history.entity.KnowledgeCardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KnowledgeCardRepository extends JpaRepository<KnowledgeCardEntity, Long> {

    Optional<KnowledgeCardEntity> findByUid(String uid);

    @Query("SELECT k FROM KnowledgeCardEntity k WHERE " +
           "LOWER(k.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(k.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<KnowledgeCardEntity> search(@Param("keyword") String keyword, Pageable pageable);
}
