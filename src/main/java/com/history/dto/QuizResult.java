package com.history.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 答题结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizResult {
    private boolean correct;
    private int pointsEarned;
    private QuestionDTO question;
    private String explanation;
}
