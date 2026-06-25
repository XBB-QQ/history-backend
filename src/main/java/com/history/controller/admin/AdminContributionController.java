package com.history.controller.admin;

import com.history.dto.ContributionDTO;
import com.history.service.ContributionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理员贡献审核 API
 */
@RestController
@RequestMapping("/api/admin/contributions")
public class AdminContributionController {

    @Autowired
    private ContributionService contributionService;

    /** 查看待审核/已审核列表 */
    @GetMapping
    public ResponseEntity<Page<ContributionDTO>> list(
            @RequestParam(defaultValue = "pending") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(contributionService.listByStatus(status, pageable));
    }

    /** 审核通过 */
    @PostMapping("/{id}/approve")
    public ResponseEntity<ContributionDTO> approve(
            @PathVariable Long id,
            @RequestParam(required = false) String comment) {
        // 管理员通过 API Key 认证，reviewerId 暂记为 0
        return ResponseEntity.ok(contributionService.approve(id, 0L, comment));
    }

    /** 审核拒绝 */
    @PostMapping("/{id}/reject")
    public ResponseEntity<ContributionDTO> reject(
            @PathVariable Long id,
            @RequestParam(required = false) String comment) {
        return ResponseEntity.ok(contributionService.reject(id, 0L, comment));
    }
}
