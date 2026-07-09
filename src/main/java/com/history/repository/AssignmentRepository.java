package com.history.repository;

import com.history.entity.AssignmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 班级任务数据访问层
 */
@Repository
public interface AssignmentRepository extends JpaRepository<AssignmentEntity, Long> {

    Optional<AssignmentEntity> findByUid(String uid);

    List<AssignmentEntity> findByTeacherNameOrderByCreatedAtDesc(String teacherName);

    List<AssignmentEntity> findByStudentNamesContaining(String studentName);
}
