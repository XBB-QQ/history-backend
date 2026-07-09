package com.history.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 创建班级任务请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentCreateRequest {

    @NotBlank(message = "任务标题不能为空")
    @Size(max = 200, message = "任务标题长度不能超过 200 字")
    private String title;

    @NotBlank(message = "路线 ID 不能为空")
    private String routeId;

    @NotBlank(message = "路线名称不能为空")
    private String routeName;

    private List<String> studentNames;

    @NotBlank(message = "教师姓名不能为空")
    private String teacherName;
}
