package com.history.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * 问答题目 DTO（出题版）
 * 安全修复 B1：出题端点不返回 correctIndex/explanation，防止前端泄题
 * 答题后通过 QuizResult.question（QuestionDTO 完整版）返回正确答案和解析
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionPublicDTO {
    private Long id;
    private String question;
    private List<String> options;
    private String difficulty;
    private String dynasty;
    private String eventId;
    private String personId;
    private String category;
}
