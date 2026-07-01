package com.history.service;

import com.history.dto.TopicDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 专题服务接口
 */
public interface TopicService {
    Page<TopicDTO> findAll(Pageable pageable);
    TopicDTO findById(Long id);
    TopicDTO findByUid(String uid);
    Page<TopicDTO> search(String keyword, Pageable pageable);
    List<String> getCategories();
    List<TopicDTO> findByCategory(String category);
    List<TopicDTO> findAllOrdered();

    /** 后台管理：创建或更新 */
    TopicDTO createOrUpdate(TopicDTO dto);
    /** 后台管理：删除 */
    void deleteById(Long id);
}
