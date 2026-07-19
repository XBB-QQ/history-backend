package com.history.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * ctext.org gettext 响应
 * 对应 ctext API 的 gettext 端点返回结构
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CtextFulltextDTO {
    /** 章节标题 */
    private String title;
    /** 全文段落列表 */
    private List<String> fulltext;
    /** 子章节 URN 列表（书目查询时返回） */
    private List<String> subsections;
    /** 错误码（如有） */
    private String errorCode;
    /** 错误描述（如有） */
    private String errorDescription;
}
