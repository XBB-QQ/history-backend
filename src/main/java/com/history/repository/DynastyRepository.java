package com.history.repository;

import com.history.entity.DynastyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DynastyRepository extends JpaRepository<DynastyEntity, Long> {

    Optional<DynastyEntity> findByUid(String uid);

    Optional<DynastyEntity> findByName(String name);

    Page<DynastyEntity> findByPeriodContaining(String keyword, Pageable pageable);
}
