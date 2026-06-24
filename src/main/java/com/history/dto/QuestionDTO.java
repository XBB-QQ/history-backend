package com.history.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * 问答题目 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO {
    private Long id;
    private String question;
    private List<String> options;
    private int correctIndex;
    private String difficulty;
    private String dynasty;
    private String eventId;
    private String personId;
    private String explanation;
    private String category;
}
