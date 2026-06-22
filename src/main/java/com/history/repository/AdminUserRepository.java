package com.history.repository;

import com.history.entity.AdminUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminUserRepository extends JpaRepository<AdminUserEntity, Long> {
    Optional<AdminUserEntity> findByUsername(String username);
    Optional<AdminUserEntity> findByApiKey(String apiKey);
    boolean existsByUsername(String username);
}
