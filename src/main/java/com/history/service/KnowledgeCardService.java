package com.history.service;

import com.history.dto.KnowledgeCardDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 知识卡片服务接口
 */
public interface KnowledgeCardService {
    Page<KnowledgeCardDTO> findAll(Pageable pageable);
    KnowledgeCardDTO findById(Long id);
    KnowledgeCardDTO findByUid(String uid);
    Page<KnowledgeCardDTO> search(String keyword, Pageable pageable);
}
