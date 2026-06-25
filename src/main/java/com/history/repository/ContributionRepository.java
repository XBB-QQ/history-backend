package com.history.repository;

import com.history.entity.ContributionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContributionRepository extends JpaRepository<ContributionEntity, Long> {

    /** 按状态查询 */
    Page<ContributionEntity> findByStatusOrderBySubmittedAtDesc(String status, Pageable pageable);

    /** 按用户查询 */
    List<ContributionEntity> findByUserIdOrderBySubmittedAtDesc(Long userId);

    /** 按类型查询 */
    Page<ContributionEntity> findByTypeOrderBySubmittedAtDesc(String type, Pageable pageable);

    /** 按用户和状态查询 */
    List<ContributionEntity> findByUserIdAndStatus(Long userId, String status);

    /** 统计用户已通过的贡献数 */
    long countByUserIdAndStatus(Long userId, String status);
}
