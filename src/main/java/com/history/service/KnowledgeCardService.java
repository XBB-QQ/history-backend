package com.history.service;

import com.history.dto.KnowledgeCardDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * 知识卡片服务接口
 */
public interface KnowledgeCardService {
    Page<KnowledgeCardDTO> findAll(Pageable pageable);
    KnowledgeCardDTO findById(Long id);
    KnowledgeCardDTO findByUid(String uid);
    Page<KnowledgeCardDTO> search(String keyword, Pageable pageable);

    /** 后台管理：创建或更新 */
    KnowledgeCardDTO createOrUpdate(KnowledgeCardDTO dto);

    /** 后台管理：删除 */
    void deleteById(Long id);

    /** 按年份排序获取所有知识卡片 */
    java.util.List<KnowledgeCardDTO> findAllOrdered();

    /** 获取标签统计（用于标签云） */
    List<Map<String, Object>> getTagStatistics();
}
