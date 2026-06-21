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
}
