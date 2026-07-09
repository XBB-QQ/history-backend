package com.history.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 班级任务出参
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentDTO {

    private Long id;
    private String uid;
    private String title;
    private String routeId;
    private String routeName;
    private List<String> studentNames;
    private String teacherName;
    private LocalDateTime createdAt;
}
