package com.history.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ctext.org 典籍搜索结果项
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CtextBookDTO {
    /** 典籍标题（繁体） */
    private String title;
    /** ctext URN（如 ctp:analects） */
    private String urn;
}
