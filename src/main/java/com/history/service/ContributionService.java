package com.history.service;

import com.history.dto.ContributionDTO;
import com.history.dto.ContributionRequest;
import com.history.entity.ContributionEntity;
import com.history.repository.ContributionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContributionService {

    @Autowired
    private ContributionRepository contributionRepository;

    /** 提交贡献 */
    public ContributionDTO submit(ContributionRequest req, Long userId, String username) {
        ContributionEntity entity = new ContributionEntity();
        entity.setType(req.getType());
        entity.setEntityId(req.getEntityId());
        entity.setTitle(req.getTitle());
        entity.setContent(req.getContent());
        entity.setChangeDescription(req.getChangeDescription());
        entity.setStatus("pending");
        entity.setUserId(userId);
        entity.setUsername(username);
        entity.setSubmittedAt(LocalDateTime.now());
        entity.setPoints(10);

        return toDTO(contributionRepository.save(entity));
    }

    /** 查询待审核列表（管理员用） */
    public Page<ContributionDTO> listByStatus(String status, Pageable pageable) {
        return contributionRepository.findByStatusOrderBySubmittedAtDesc(status, pageable)
                .map(this::toDTO);
    }

    /** 按类型查询 */
    public Page<ContributionDTO> listByType(String type, Pageable pageable) {
        return contributionRepository.findByTypeOrderBySubmittedAtDesc(type, pageable)
                .map(this::toDTO);
    }

    /** 查询用户的贡献 */
    public List<ContributionDTO> listByUser(Long userId) {
        return contributionRepository.findByUserIdOrderBySubmittedAtDesc(userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /** 审核通过 */
    public ContributionDTO approve(Long id, Long reviewerId, String comment) {
        ContributionEntity entity = contributionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("贡献不存在"));
        entity.setStatus("approved");
        entity.setReviewerId(reviewerId);
        entity.setReviewComment(comment);
        entity.setReviewedAt(LocalDateTime.now());
        return toDTO(contributionRepository.save(entity));
    }

    /** 审核拒绝 */
    public ContributionDTO reject(Long id, Long reviewerId, String comment) {
        ContributionEntity entity = contributionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("贡献不存在"));
        entity.setStatus("rejected");
        entity.setReviewerId(reviewerId);
        entity.setReviewComment(comment);
        entity.setReviewedAt(LocalDateTime.now());
        entity.setPoints(0);
        return toDTO(contributionRepository.save(entity));
    }

    /** 统计用户已通过的贡献数 */
    public long countApprovedByUser(Long userId) {
        return contributionRepository.countByUserIdAndStatus(userId, "approved");
    }

    private ContributionDTO toDTO(ContributionEntity e) {
        ContributionDTO dto = new ContributionDTO();
        dto.setId(e.getId());
        dto.setType(e.getType());
        dto.setEntityId(e.getEntityId());
        dto.setTitle(e.getTitle());
        dto.setContent(e.getContent());
        dto.setChangeDescription(e.getChangeDescription());
        dto.setStatus(e.getStatus());
        dto.setUserId(e.getUserId());
        dto.setUsername(e.getUsername());
        dto.setReviewerId(e.getReviewerId());
        dto.setReviewComment(e.getReviewComment());
        dto.setSubmittedAt(e.getSubmittedAt());
        dto.setReviewedAt(e.getReviewedAt());
        dto.setPoints(e.getPoints());
        return dto;
    }
}
