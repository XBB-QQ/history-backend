package com.history.service;

import com.history.dto.PersonDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 人物服务接口
 */
public interface PersonService {
    Page<PersonDTO> findAll(Pageable pageable);
    PersonDTO findById(Long id);
    PersonDTO findByUid(String uid);
    Page<PersonDTO> search(String keyword, Pageable pageable);
    Page<PersonDTO> findByGender(String gender, Pageable pageable);
    Page<PersonDTO> findByDynasty(String dynastyName, Pageable pageable);
    Page<PersonDTO> findByRole(String role, Pageable pageable);

    /** 后台管理：创建或更新 */
    PersonDTO createOrUpdate(PersonDTO dto);

    /** 后台管理：删除 */
    void deleteById(Long id);

    /** 按年代排序获取所有人物 */
    java.util.List<PersonDTO> findAllOrdered();
}
