-- 一次性修复脚本：重置 V20260718.001 让 Flyway 重跑
-- 背景：V20260718.001 已执行但建表用了 BLOB，与 Entity (@Lob byte[] 期望 LONGBLOB) 冲突
--       导致 Hibernate validate 失败，应用启动报错
-- 用法：用任意 MySQL 客户端连接 localhost:3306/history_museum 执行本脚本
-- 风险：rag_vectors 表当前为空（启动失败没灌数据），DROP 无损失
-- 执行后：重启后端，Flyway 会以 v20260718.001 重跑（已是 LONGBLOB 版本）

-- 1. 删除建错的表
DROP TABLE IF EXISTS rag_vectors;

-- 2. 删除 Flyway 历史中 V20260718.001 的执行记录
DELETE FROM flyway_schema_history
WHERE version = '20260718.001'
  AND description = 'create rag vectors';

-- 3. 验证：应显示 0 行
SELECT COUNT(*) AS remaining_v20260718_records
FROM flyway_schema_history
WHERE version = '20260718.001';
