package com.history.service;

import com.history.dto.QuizResult;
import com.history.dto.QuestionDTO;
import com.history.dto.QuestionPublicDTO;
import com.history.dto.UserDTO;
import org.springframework.data.domain.Page;

public interface QuizService {

    /** 获取每日题目（出题版，不含 correctIndex/explanation，防泄题） */
    QuestionPublicDTO getDailyQuestion();

    /** 提交答案（返回完整 QuestionDTO + explanation） */
    QuizResult submitAnswer(Long questionId, int selectedIndex, String userId);

    /** 获取随机题目（出题版，用于练习模式） */
    Page<QuestionPublicDTO> getRandomQuestions(int page, int size);

    /** 获取排行榜 */
    Page<UserDTO> getLeaderboard(int page, int size);
}
