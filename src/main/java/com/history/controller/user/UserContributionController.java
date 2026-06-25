package com.history.controller.user;

import com.history.dto.ContributionDTO;
import com.history.dto.ContributionRequest;
import com.history.entity.UserEntity;
import com.history.repository.UserRepository;
import com.history.service.ContributionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户贡献 API — 协作式知识贡献
 */
@RestController
@RequestMapping("/api/user/contributions")
public class UserContributionController {

    @Autowired
    private ContributionService contributionService;

    @Autowired
    private UserRepository userRepository;

    /** 从 SecurityContext 获取当前登录用户 */
    private UserEntity getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null) {
            return null;
        }
        String username = auth.getName();
        return userRepository.findByUsername(username).orElse(null);
    }

    /** 提交贡献 */
    @PostMapping
    public ResponseEntity<?> submit(@RequestBody ContributionRequest req) {
        UserEntity user = getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "请先登录"));
        }

        // 参数校验
        if (req.getType() == null || req.getTitle() == null || req.getContent() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "类型、标题、内容不能为空"));
        }

        ContributionDTO dto = contributionService.submit(req, user.getId(), user.getUsername());
        return ResponseEntity.ok(dto);
    }

    /** 查看我的贡献列表 */
    @GetMapping("/mine")
    public ResponseEntity<?> myContributions() {
        UserEntity user = getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "请先登录"));
        }

        List<ContributionDTO> list = contributionService.listByUser(user.getId());

        Map<String, Object> result = new HashMap<>();
        result.put("contributions", list);
        result.put("total", list.size());
        result.put("approved", contributionService.countApprovedByUser(user.getId()));
        return ResponseEntity.ok(result);
    }
}
