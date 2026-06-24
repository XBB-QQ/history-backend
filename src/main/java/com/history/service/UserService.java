package com.history.service;

import com.history.dto.LoginRequest;
import com.history.dto.RegisterRequest;
import com.history.dto.UserDTO;

public interface UserService {
    /** 注册新用户 */
    UserDTO register(RegisterRequest request);
    /** 用户登录，返回 JWT Token */
    String login(LoginRequest request);
    /** 获取用户信息 */
    UserDTO getUserByUsername(String username);
    /** 更新用户资料 */
    UserDTO updateUser(String username, UserDTO dto);
}
