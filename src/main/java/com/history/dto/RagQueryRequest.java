package com.history.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RAG 查询请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RagQueryRequest {

    @NotBlank(message = "问题不能为空")
    @Size(max = 500, message = "问题长度不能超过 500 字")
    private String question;

    /** 可选：限制检索的文档类型（event/person/dynasty/knowledge/topic） */
    private String docType;
}
