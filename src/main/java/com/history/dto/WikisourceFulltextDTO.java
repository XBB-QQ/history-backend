package com.history.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Wikisource 全文响应
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WikisourceFulltextDTO {
    /** 页面标题 */
    private String title;
    /** 已解析的纯文本（去除 wikitext 标记） */
    private String fulltext;
    /** 子章节标题列表（解析 wikitext 链接得到） */
    private List<String> subsections;
    /** 错误码（如有） */
    private String errorCode;
    /** 错误描述（如有） */
    private String errorDescription;
}
