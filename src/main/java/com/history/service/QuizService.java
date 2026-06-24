package com.history.service;

import com.history.dto.QuizResult;
import com.history.dto.QuestionDTO;
import com.history.dto.UserDTO;
import org.springframework.data.domain.Page;

public interface QuizService {

    /** 获取每日题目 */
    QuestionDTO getDailyQuestion();

    /** 提交答案 */
    QuizResult submitAnswer(Long questionId, int selectedIndex, String userId);

    /** 获取随机题目（用于练习模式） */
    Page<QuestionDTO> getRandomQuestions(int page, int size);

    /** 获取排行榜 */
    Page<UserDTO> getLeaderboard(int page, int size);
}
