package com.history.repository;

import com.history.entity.AssignmentProgressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 班级任务进度数据访问层
 */
@Repository
public interface AssignmentProgressRepository extends JpaRepository<AssignmentProgressEntity, Long> {

    List<AssignmentProgressEntity> findByAssignmentId(Long assignmentId);

    Optional<AssignmentProgressEntity> findByAssignmentIdAndStudentName(Long assignmentId, String studentName);
}
