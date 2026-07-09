package com.history.service.impl;

import com.history.dto.AssignmentCreateRequest;
import com.history.dto.AssignmentDTO;
import com.history.dto.AssignmentProgressDTO;
import com.history.dto.ProgressUpdateRequest;
import com.history.entity.AssignmentEntity;
import com.history.entity.AssignmentProgressEntity;
import com.history.exception.ResourceNotFoundException;
import com.history.repository.AssignmentProgressRepository;
import com.history.repository.AssignmentRepository;
import com.history.service.ClassroomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClassroomServiceImpl implements ClassroomService {

    private final AssignmentRepository assignmentRepository;
    private final AssignmentProgressRepository progressRepository;

    @Override
    @Transactional
    public AssignmentDTO createAssignment(AssignmentCreateRequest request) {
        AssignmentEntity entity = AssignmentEntity.builder()
                .uid("asg-" + UUID.randomUUID().toString().substring(0, 8))
                .title(request.getTitle())
                .routeId(request.getRouteId())
                .routeName(request.getRouteName())
                .studentNames(request.getStudentNames() != null ? new ArrayList<>(request.getStudentNames()) : new ArrayList<>())
                .teacherName(request.getTeacherName())
                .build();
        return toDTO(assignmentRepository.save(entity));
    }

    @Override
    public List<AssignmentDTO> listAssignments(String teacherName, String studentName) {
        if (teacherName != null && !teacherName.isBlank()) {
            return assignmentRepository.findByTeacherNameOrderByCreatedAtDesc(teacherName)
                    .stream().map(this::toDTO).toList();
        }
        if (studentName != null && !studentName.isBlank()) {
            return assignmentRepository.findByStudentNamesContaining(studentName)
                    .stream().map(this::toDTO).toList();
        }
        return assignmentRepository.findAll()
                .stream().map(this::toDTO).toList();
    }

    @Override
    public AssignmentDTO getAssignment(String uid) {
        return assignmentRepository.findByUid(uid)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment", uid));
    }

    @Override
    @Transactional
    public void deleteAssignment(String uid) {
        AssignmentEntity entity = assignmentRepository.findByUid(uid)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment", uid));
        List<AssignmentProgressEntity> progresses = progressRepository.findByAssignmentId(entity.getId());
        if (!progresses.isEmpty()) {
            progressRepository.deleteAll(progresses);
        }
        assignmentRepository.delete(entity);
    }

    @Override
    public List<AssignmentProgressDTO> getProgress(String uid) {
        AssignmentEntity entity = assignmentRepository.findByUid(uid)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment", uid));
        return progressRepository.findByAssignmentId(entity.getId())
                .stream().map(this::toProgressDTO).toList();
    }

    @Override
    @Transactional
    public AssignmentProgressDTO updateProgress(String uid, ProgressUpdateRequest request) {
        AssignmentEntity entity = assignmentRepository.findByUid(uid)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment", uid));

        AssignmentProgressEntity progress = progressRepository
                .findByAssignmentIdAndStudentName(entity.getId(), request.getStudentName())
                .orElse(AssignmentProgressEntity.builder()
                        .assignmentId(entity.getId())
                        .studentName(request.getStudentName())
                        .completedNodes(new ArrayList<>())
                        .build());

        List<String> incomingNodes = request.getCompletedNodes() != null
                ? new ArrayList<>(new LinkedHashSet<>(request.getCompletedNodes()))
                : new ArrayList<>();
        progress.setCompletedNodes(incomingNodes);
        progress.setLastActiveAt(LocalDateTime.now());

        return toProgressDTO(progressRepository.save(progress));
    }

    private AssignmentDTO toDTO(AssignmentEntity entity) {
        return AssignmentDTO.builder()
                .id(entity.getId())
                .uid(entity.getUid())
                .title(entity.getTitle())
                .routeId(entity.getRouteId())
                .routeName(entity.getRouteName())
                .studentNames(entity.getStudentNames())
                .teacherName(entity.getTeacherName())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private AssignmentProgressDTO toProgressDTO(AssignmentProgressEntity entity) {
        return AssignmentProgressDTO.builder()
                .assignmentId(entity.getAssignmentId())
                .studentName(entity.getStudentName())
                .completedNodes(entity.getCompletedNodes())
                .lastActiveAt(entity.getLastActiveAt())
                .build();
    }
}
