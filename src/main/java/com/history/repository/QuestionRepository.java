package com.history.repository;

import com.history.entity.QuestionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<QuestionEntity, Long> {

    /** 按难度获取题目 */
    Page<QuestionEntity> findByDifficulty(String difficulty, Pageable pageable);

    /** 按朝代获取题目 */
    Page<QuestionEntity> findByDynasty(String dynasty, Pageable pageable);

    /** 随机获取一道题目 */
    @Query("SELECT q FROM QuestionEntity q ORDER BY FUNCTION('RAND')")
    Page<QuestionEntity> findRandom(Pageable pageable);

    /** 按分类获取题目 */
    Page<QuestionEntity> findByCategory(String category, Pageable pageable);

    /** 按分类和难度获取 */
    Page<QuestionEntity> findByCategoryAndDifficulty(String category, String difficulty, Pageable pageable);
}
