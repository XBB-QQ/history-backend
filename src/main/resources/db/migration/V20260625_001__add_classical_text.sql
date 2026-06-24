-- 史书原文引用层 — 扩展事件表
-- 为事件添加史书原文、出处、白话译文字段
-- @see history-museum/design/000-future-roadmap.md §方向二 §2.1

-- 注意：应用使用 Hibernate ddl-auto=update，启动时会自动 ALTER TABLE 添加新字段
-- 本脚本用于手动初始化或 Flyway 启用时使用

ALTER TABLE events
    ADD COLUMN classical_text TEXT COMMENT '史书原文片段（古文）' AFTER related_articles,
    ADD COLUMN classical_source VARCHAR(200) COMMENT '史书出处（如《史记·秦始皇本纪》）' AFTER classical_text,
    ADD COLUMN modern_translation TEXT COMMENT '白话译文' AFTER classical_source;
