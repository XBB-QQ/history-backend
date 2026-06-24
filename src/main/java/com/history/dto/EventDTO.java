package com.history.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 事件数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDTO {
    private Long id;
    private String uid;
    private String title;
    private Integer year;
    private String yearDisplay;
    private String yearPrecision;
    private String category;
    private String dynastyName;
    private String description;
    private String fulltext;
    private List<String> tags;
    private List<String> relatedEvents;
    private List<String> relatedPersons;
    private String impact;
    private Integer significance;
    private List<String> relatedArticles;
    /** 史书原文片段（古文） */
    private String classicalText;
    /** 史书出处（如《史记·秦始皇本纪》） */
    private String classicalSource;
    /** 白话译文 */
    private String modernTranslation;
}
