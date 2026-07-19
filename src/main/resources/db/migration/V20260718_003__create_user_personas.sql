-- 用户 AI 画像持久化表
-- 前端 personaStore 的 UserPersona JSON 整体存到后端，实现跨设备同步
--
-- API：
--   GET    /api/users/me/persona
--   PUT    /api/users/me/persona
--   DELETE /api/users/me/persona
--
-- 字段说明：
--   username      关联 users.username（与 favorites 表保持一致用 username 作 key）
--   persona_json  整个 UserPersona JSON 字符串（前端 store 序列化结果）
--   updated_at    最后更新时间

CREATE TABLE user_personas (
    id           BIGINT      NOT NULL AUTO_INCREMENT,
    username     VARCHAR(50) NOT NULL COMMENT '用户名（关联 users.username）',
    persona_json TEXT        NOT NULL COMMENT 'UserPersona JSON 字符串',
    updated_at   TIMESTAMP   NOT NULL COMMENT '最后更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_persona_username (username)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '用户 AI 画像持久化';
