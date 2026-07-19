package com.history.service.impl;

import com.history.dto.*;
import com.history.entity.QuestionEntity;
import com.history.entity.UserEntity;
import com.history.repository.QuestionRepository;
import com.history.repository.UserRepository;
import com.history.service.QuizService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Override
    public QuestionPublicDTO getDailyQuestion() {
        // 根据当天日期确定种子，保证每天同一题
        LocalDate today = LocalDate.now();
        long seed = today.atStartOfDay().toEpochSecond(java.time.ZoneOffset.UTC);

        // 使用 hash(seed) % total 来选择题目
        PageRequest page = PageRequest.of(0, 1);
        Page<QuestionEntity> all = questionRepository.findAll(page);
        if (all.isEmpty() || all.getTotalElements() == 0) {
            throw new RuntimeException("暂无题目");
        }

        int index = (int) (Math.abs(hash(seed)) % all.getTotalElements());
        List<QuestionEntity> questions = all.getContent();
        // 如果第一页不够，分页获取
        while (index >= questions.size()) {
            Page<QuestionEntity> next = questionRepository.findAll(PageRequest.of(questions.size() / 20 + 1, 20));
            questions.addAll(next.getContent());
        }

        QuestionEntity entity = questions.get(index);
        // 安全修复 B1：出题端点不返回 correctIndex/explanation
        return toPublicDTO(entity);
    }

    @Override
    @Transactional
    public QuizResult submitAnswer(Long questionId, int selectedIndex, String userId) {
        QuestionEntity entity = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("题目不存在"));

        boolean correct = selectedIndex == entity.getCorrectIndex();

        // 积分计算
        int points;
        if (correct) {
            String diff = entity.getDifficulty();
            if ("easy".equals(diff)) points = 10;
            else if ("medium".equals(diff)) points = 20;
            else if ("hard".equals(diff)) points = 30;
            else points = 0;
        } else {
            points = 0;
        }

        final int finalPoints = points;

        // 更新用户积分
        if (correct && userId != null && !userId.isBlank()) {
            userRepository.findByUsername(userId).ifPresent(user -> {
                user.setScore(user.getScore() + finalPoints);
                user.setQuizzesAnswered(user.getQuizzesAnswered() + 1);
                user.setQuizzesCorrect(user.getQuizzesCorrect() + 1);
                userRepository.save(user);
            });
        } else if (!correct && userId != null && !userId.isBlank()) {
            userRepository.findByUsername(userId).ifPresent(user -> {
                user.setQuizzesAnswered(user.getQuizzesAnswered() + 1);
                userRepository.save(user);
            });
        }

        return new QuizResult(correct, points, toDTO(entity), entity.getExplanation());
    }

    @Override
    public Page<QuestionPublicDTO> getRandomQuestions(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        // 安全修复 B1：出题端点不返回 correctIndex/explanation
        return questionRepository.findRandom(pageRequest).map(this::toPublicDTO);
    }

    @Override
    public Page<UserDTO> getLeaderboard(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "score"));
        return userRepository.findAll(pageRequest).map(this::toUserDTO);
    }

    private UserDTO toUserDTO(UserEntity user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setNickname(user.getNickname());
        dto.setRole(user.getRole());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setBio(user.getBio());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setLastLoginAt(user.getLastLoginAt());
        dto.setScore(user.getScore());
        dto.setQuizzesAnswered(user.getQuizzesAnswered());
        dto.setQuizzesCorrect(user.getQuizzesCorrect());
        return dto;
    }

    private QuestionDTO toDTO(QuestionEntity entity) {
        List<String> options;
        try {
            options = objectMapper.readValue(entity.getOptions(), new TypeReference<List<String>>() {});
        } catch (Exception e) {
            options = List.of("解析错误", "", "", "");
        }

        QuestionDTO dto = new QuestionDTO();
        dto.setId(entity.getId());
        dto.setQuestion(entity.getQuestion());
        dto.setOptions(options);
        dto.setCorrectIndex(entity.getCorrectIndex());
        dto.setDifficulty(entity.getDifficulty());
        dto.setDynasty(entity.getDynasty());
        dto.setEventId(entity.getEventId());
        dto.setPersonId(entity.getPersonId());
        dto.setExplanation(entity.getExplanation());
        dto.setCategory(entity.getCategory());
        return dto;
    }

    /** 安全修复 B1：出题端点用此方法，不含 correctIndex/explanation */
    private QuestionPublicDTO toPublicDTO(QuestionEntity entity) {
        List<String> options;
        try {
            options = objectMapper.readValue(entity.getOptions(), new TypeReference<List<String>>() {});
        } catch (Exception e) {
            options = List.of("解析错误", "", "", "");
        }

        QuestionPublicDTO dto = new QuestionPublicDTO();
        dto.setId(entity.getId());
        dto.setQuestion(entity.getQuestion());
        dto.setOptions(options);
        dto.setDifficulty(entity.getDifficulty());
        dto.setDynasty(entity.getDynasty());
        dto.setEventId(entity.getEventId());
        dto.setPersonId(entity.getPersonId());
        dto.setCategory(entity.getCategory());
        return dto;
    }

    /** 简单 hash 函数 */
    private long hash(long seed) {
        seed ^= seed >>> 33;
        seed *= 0xff51afd7ed558ccdL;
        seed ^= seed >>> 33;
        seed *= 0xc4ceb9fe1a85ec53L;
        seed ^= seed >>> 33;
        return seed;
    }
}
