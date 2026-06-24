package com.history.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 阅读清单
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadingListDTO {
    private Long id;
    private String name;
    private String description;
    private List<ResourceItem> resources;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResourceItem {
        private String type;  // event/person/dynasty/knowledge
        private Long id;
        private String title;
    }
}
