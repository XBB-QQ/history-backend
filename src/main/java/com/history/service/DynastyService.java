package com.history.service;

import com.history.dto.DynastyDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 朝代服务接口
 */
public interface DynastyService {
    Page<DynastyDTO> findAll(Pageable pageable);
    DynastyDTO findById(Long id);
    DynastyDTO findByUid(String uid);
    DynastyDTO findByName(String name);

    /** 后台管理：创建或更新 */
    DynastyDTO createOrUpdate(DynastyDTO dto);

    /** 后台管理：删除 */
    void deleteById(Long id);

    /** 按年代排序获取所有朝代 */
    java.util.List<DynastyDTO> findAllOrdered();
}
