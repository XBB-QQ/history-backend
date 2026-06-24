package com.history.service;

import com.history.dto.EventDTO;
import com.history.dto.EventRelatedDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 事件服务接口
 */
public interface EventService {
    Page<EventDTO> findAll(Pageable pageable);
    List<EventDTO> findAllOrdered();
    EventDTO findById(Long id);
    EventDTO findByUid(String uid);
    Page<EventDTO> search(String keyword, Pageable pageable);
    Page<EventDTO> findByCategory(String category, Pageable pageable);
    Page<EventDTO> findByDynasty(String dynastyName, Pageable pageable);
    Page<EventDTO> findByYearRange(Integer start, Integer end, Pageable pageable);

    /** 后台管理：创建或更新 */
    EventDTO createOrUpdate(EventDTO dto);

    /** 后台管理：删除 */
    void deleteById(Long id);

    /** 获取事件关联数据（人物 + 知识卡片） */
    EventRelatedDTO getRelatedData(Long id);
}
