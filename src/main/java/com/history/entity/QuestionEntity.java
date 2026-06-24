package com.history.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 问答题目实体
 */
@Entity
@Table(name = "questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 题目内容 */
    @Column(nullable = false, length = 500)
    private String question;

    /** 选项 JSON 数组：["选项A", "选项B", "选项C", "选项D"] */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String options;

    /** 正确答案索引（0-3） */
    @Column(nullable = false)
    private int correctIndex;

    /** 难度：easy / medium / hard */
    @Column(nullable = false, length = 20)
    private String difficulty;

    /** 关联朝代 */
    @Column(length = 100)
    private String dynasty;

    /** 关联事件 UID */
    @Column(length = 100)
    private String eventId;

    /** 关联人物 UID */
    @Column(length = 100)
    private String personId;

    /** 题目解析/解释 */
    @Column(columnDefinition = "TEXT")
    private String explanation;

    /** 分类：军事/政治/文化/科技 */
    @Column(length = 50)
    private String category;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
