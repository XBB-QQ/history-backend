package com.history.controller;

import com.history.dto.QuizResult;
import com.history.dto.QuestionPublicDTO;
import com.history.dto.UserDTO;
import com.history.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 问答挑战 API
 */
@RestController
@RequestMapping("/api/user/quiz")
@RequiredArgsConstructor
@Tag(name = "问答挑战", description = "历史问答题目、积分和排行榜")
public class QuizController {

    private final QuizService quizService;

    /** 安全修复 B2：从 SecurityContext 取当前登录用户名（JWT 解析后已设置） */
    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null) {
            return null;
        }
        return auth.getName();
    }

    @GetMapping("/daily")
    @Operation(summary = "获取每日题目")
    public ResponseEntity<QuestionPublicDTO> getDailyQuestion() {
        // 安全修复 B1：出题端点返回 QuestionPublicDTO（不含 correctIndex/explanation）
        return ResponseEntity.ok(quizService.getDailyQuestion());
    }

    @PostMapping("/answer")
    @Operation(summary = "提交答案")
    public ResponseEntity<QuizResult> submitAnswer(
            @RequestBody com.history.dto.QuizAnswerRequest request) {
        // 安全修复 B2：删除 @RequestHeader("X-User-Id")，改用 SecurityContext 取 username
        String userId = getCurrentUsername();
        QuizResult result = quizService.submitAnswer(request.getQuestionId(), request.getSelectedIndex(), userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/random")
    @Operation(summary = "获取随机题目（练习模式）")
    public ResponseEntity<Page<QuestionPublicDTO>> getRandomQuestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        // 安全修复 B1：出题端点返回 Page<QuestionPublicDTO>
        return ResponseEntity.ok(quizService.getRandomQuestions(page, size));
    }

    @GetMapping("/ranking")
    @Operation(summary = "积分排行榜")
    public ResponseEntity<Page<UserDTO>> getLeaderboard(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(quizService.getLeaderboard(page, size));
    }
}

