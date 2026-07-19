-- RAG 向量持久化存储表
-- 将向量从内存迁移到 MySQL，避免每次重启后端都调用 embedding API 重建索引
--
-- 字段说明：
--   id        向量唯一标识（如 event-332, person-57, dynasty-1, knowledge-10）
--   vector    float[] 的二进制数据，每 float 4 字节，1024 维 = 4096 字节
--             用 LONGBLOB：Hibernate 6 + MySQL 8 对 @Lob byte[] 默认期望 LONGBLOB，
--             且 BLOB (64KB) 虽够用但 Hibernate validate 会因类型映射歧义报错
--   metadata  JSON 字符串（type/title/content/source/year 等）

CREATE TABLE rag_vectors (
    id         VARCHAR(128) NOT NULL,
    vector     LONGBLOB     NOT NULL COMMENT 'float[] 二进制数据，每 float 4 字节',
    metadata   TEXT         COMMENT 'JSON 元数据',
    created_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = 'RAG 向量持久化存储';
