package com.history.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Wikisource 搜索结果项
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WikisourceBookDTO {
    /** 页面标题（繁体） */
    private String title;
    /** 页面 ID */
    private long pageId;
    /** 摘要片段（含 HTML 高亮） */
    private String snippet;
}
