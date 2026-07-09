package com.history.service;

import com.history.dto.AssignmentCreateRequest;
import com.history.dto.AssignmentDTO;
import com.history.dto.AssignmentProgressDTO;
import com.history.dto.ProgressUpdateRequest;

import java.util.List;

/**
 * 班级史馆服务接口
 */
public interface ClassroomService {

    AssignmentDTO createAssignment(AssignmentCreateRequest request);

    List<AssignmentDTO> listAssignments(String teacherName, String studentName);

    AssignmentDTO getAssignment(String uid);

    void deleteAssignment(String uid);

    List<AssignmentProgressDTO> getProgress(String uid);

    AssignmentProgressDTO updateProgress(String uid, ProgressUpdateRequest request);
}
