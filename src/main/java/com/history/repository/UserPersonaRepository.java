package com.history.repository;

import com.history.entity.UserPersonaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户 AI 画像 Repository
 */
@Repository
public interface UserPersonaRepository extends JpaRepository<UserPersonaEntity, Long> {

    /** 按用户名查询画像 */
    Optional<UserPersonaEntity> findByUsername(String username);

    /** 是否存在 */
    boolean existsByUsername(String username);

    /** 删除 */
    void deleteByUsername(String username);
}
