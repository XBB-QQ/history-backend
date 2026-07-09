package com.history.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 更新学生进度请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgressUpdateRequest {

    @NotBlank(message = "学生姓名不能为空")
    private String studentName;

    private List<String> completedNodes;
}
