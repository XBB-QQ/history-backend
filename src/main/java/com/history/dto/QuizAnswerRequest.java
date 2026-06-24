package com.history.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 答题请求
 */
@Data
@NoArgsConstructor
public class QuizAnswerRequest {
    private Long questionId;
    private int selectedIndex;
}
