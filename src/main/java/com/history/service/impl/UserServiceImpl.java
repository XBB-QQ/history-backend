package com.history.service.impl;

import com.history.dto.*;
import com.history.entity.UserEntity;
import com.history.repository.UserRepository;
import com.history.service.UserService;
import com.history.util.JwtUtil;
import com.history.util.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDTO register(RegisterRequest request) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已被注册");
        }

        // 生成盐值和密码哈希
        String salt = SecurityUtil.generateSalt();
        String passwordHash = SecurityUtil.sha256(request.getPassword(), salt);

        // 创建用户实体
        UserEntity user = new UserEntity();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordHash);
        user.setSalt(salt);
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole("user");

        UserEntity saved = userRepository.save(user);
        return toDTO(saved);
    }

    @Override
    public String login(LoginRequest request) {
        UserEntity user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));

        // 验证密码
        String hash = SecurityUtil.sha256(request.getPassword(), user.getSalt());
        if (!hash.equals(user.getPasswordHash())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 更新最后登录时间
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        // 生成 Token
        return JwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
    }

    @Override
    public UserDTO getUserByUsername(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return toDTO(user);
    }

    @Override
    @Transactional
    public UserDTO updateUser(String username, UserDTO dto) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (dto.getNickname() != null) user.setNickname(dto.getNickname());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        if (dto.getBio() != null) user.setBio(dto.getBio());
        if (dto.getAvatarUrl() != null) user.setAvatarUrl(dto.getAvatarUrl());

        UserEntity saved = userRepository.save(user);
        return toDTO(saved);
    }

    /** Entity → DTO 转换（不包含密码） */
    private UserDTO toDTO(UserEntity user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setNickname(user.getNickname());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
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
}
