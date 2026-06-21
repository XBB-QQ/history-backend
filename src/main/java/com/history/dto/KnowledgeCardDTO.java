package com.history.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 知识卡片数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KnowledgeCardDTO {
    private Long id;
    private String uid;
    private String title;
    private Integer startYear;
    private String startYearDisplay;
    private String dynastyName;
    private String description;
    private String fulltext;
    private List<String> tags;
    private List<String> relevantEvents;
    private List<String> relevantPersons;
    private String meta;
}
