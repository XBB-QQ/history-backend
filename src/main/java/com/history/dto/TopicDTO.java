package com.history.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 专题数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopicDTO {
    private Long id;
    private String uid;
    private String title;
    private String category;
    private String coverImage;
    private String summary;
    private String description;
    private Integer chapterCount;
    private Integer estimatedMinutes;
    private List<String> tags;
    private List<String> relatedEvents;
    private List<String> relatedPersons;
    private String chapters;
    private List<String> references;
    private Integer sortOrder;
    private Boolean published;
}
