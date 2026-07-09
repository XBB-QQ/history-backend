package com.history.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 班级任务进度出参
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentProgressDTO {

    private Long assignmentId;
    private String studentName;
    private List<String> completedNodes;
    private LocalDateTime lastActiveAt;
}
