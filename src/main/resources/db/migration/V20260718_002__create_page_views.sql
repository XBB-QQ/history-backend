-- 页面访问埋点表
-- 前端在路由切换时 POST /api/page-view 上报，后端异步落库
-- GET /api/page-view/hot 查询热度榜
--
-- 字段说明：
--   page_path    页面路径（如 /timeline, /persons），不含 query string
--   user_id      已登录用户 ID，未登录为 NULL
--   session_id   前端 localStorage 生成的 UUID，用于追踪匿名会话
--   visited_at   访问时间
--
-- 索引：
--   idx_page_path   热度榜 GROUP BY 用
--   idx_visited_at  时间范围查询用（未来扩展）
--   idx_session_id  会话去重分析用

CREATE TABLE page_views (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    page_path   VARCHAR(128) NOT NULL COMMENT '页面路径',
    user_id     VARCHAR(64)  COMMENT '用户 ID，未登录为 NULL',
    session_id  VARCHAR(64)  NOT NULL COMMENT '前端 localStorage 生成的会话 UUID',
    visited_at  TIMESTAMP    NOT NULL COMMENT '访问时间',
    PRIMARY KEY (id),
    INDEX idx_page_path (page_path),
    INDEX idx_visited_at (visited_at),
    INDEX idx_session_id (session_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '页面访问埋点记录';
