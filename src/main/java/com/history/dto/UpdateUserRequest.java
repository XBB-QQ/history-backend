package com.history.dto;

/**
 * 更新用户信息请求（不含敏感字段如 role）
 */
public class UpdateUserRequest {
    private String nickname;
    private String email;
    private String phone;
    private String bio;
    private String avatarUrl;

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    /** 转换为 UserDTO（不包含 id/username/role 等敏感字段） */
    public UserDTO toDTO() {
        UserDTO dto = new UserDTO();
        dto.setNickname(nickname);
        dto.setEmail(email);
        dto.setPhone(phone);
        dto.setBio(bio);
        dto.setAvatarUrl(avatarUrl);
        return dto;
    }
}
