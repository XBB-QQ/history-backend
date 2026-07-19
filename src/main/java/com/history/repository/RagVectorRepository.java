package com.history.repository;

import com.history.entity.RagVectorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * RAG 向量持久化 Repository
 * <p>
 * 启动时通过 findAll() 一次性加载所有向量到内存；
 * 写入时通过 save()/saveAll() 持久化到 MySQL；
 * 清空时通过 deleteAllInBatch()（JpaRepository 自带）。
 */
public interface RagVectorRepository extends JpaRepository<RagVectorEntity, String> {
}
