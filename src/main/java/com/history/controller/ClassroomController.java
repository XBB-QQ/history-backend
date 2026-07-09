package com.history.controller;

import com.history.dto.AssignmentCreateRequest;
import com.history.dto.AssignmentDTO;
import com.history.dto.AssignmentProgressDTO;
import com.history.dto.ProgressUpdateRequest;
import com.history.service.ClassroomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 班级史馆 API 控制器
 */
@RestController
@RequestMapping("/api/classroom")
@RequiredArgsConstructor
@Tag(name = "班级史馆", description = "班级任务与进度管理接口")
public class ClassroomController {

    private final ClassroomService classroomService;

    @PostMapping("/assignments")
    @Operation(summary = "创建班级任务")
    public ResponseEntity<AssignmentDTO> createAssignment(@RequestBody @Valid AssignmentCreateRequest request) {
        return ResponseEntity.ok(classroomService.createAssignment(request));
    }

    @GetMapping("/assignments")
    @Operation(summary = "查询任务列表（按教师或学生筛选）")
    public ResponseEntity<List<AssignmentDTO>> listAssignments(
            @RequestParam(required = false) String teacherName,
            @RequestParam(required = false) String studentName) {
        return ResponseEntity.ok(classroomService.listAssignments(teacherName, studentName));
    }

    @GetMapping("/assignments/{uid}")
    @Operation(summary = "获取任务详情")
    public ResponseEntity<AssignmentDTO> getAssignment(@PathVariable String uid) {
        return ResponseEntity.ok(classroomService.getAssignment(uid));
    }

    @DeleteMapping("/assignments/{uid}")
    @Operation(summary = "删除任务")
    public ResponseEntity<Void> deleteAssignment(@PathVariable String uid) {
        classroomService.deleteAssignment(uid);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/assignments/{uid}/progress")
    @Operation(summary = "获取任务进度列表")
    public ResponseEntity<List<AssignmentProgressDTO>> getProgress(@PathVariable String uid) {
        return ResponseEntity.ok(classroomService.getProgress(uid));
    }

    @PutMapping("/assignments/{uid}/progress")
    @Operation(summary = "更新学生进度（upsert，合并已完成的节点）")
    public ResponseEntity<AssignmentProgressDTO> updateProgress(
            @PathVariable String uid,
            @RequestBody @Valid ProgressUpdateRequest request) {
        return ResponseEntity.ok(classroomService.updateProgress(uid, request));
    }
}
