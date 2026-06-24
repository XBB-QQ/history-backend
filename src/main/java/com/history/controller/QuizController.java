package com.history.controller;

import com.history.dto.QuizResult;
import com.history.dto.QuestionDTO;
import com.history.dto.UserDTO;
import com.history.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/daily")
    @Operation(summary = "获取每日题目")
    public ResponseEntity<QuestionDTO> getDailyQuestion() {
        return ResponseEntity.ok(quizService.getDailyQuestion());
    }

    @PostMapping("/answer")
    @Operation(summary = "提交答案")
    public ResponseEntity<QuizResult> submitAnswer(
            @RequestBody com.history.dto.QuizAnswerRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        QuizResult result = quizService.submitAnswer(request.getQuestionId(), request.getSelectedIndex(), userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/random")
    @Operation(summary = "获取随机题目（练习模式）")
    public ResponseEntity<Page<QuestionDTO>> getRandomQuestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
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
