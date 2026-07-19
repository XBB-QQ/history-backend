package com.history.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 汉字演变响应 — 与前端 CharEvolution 接口对齐
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharEvolutionResponse {

    /** 汉字（char 是 Java 关键字，序列化时映射为 "char"） */
    @JsonProperty("char")
    private String character;
    private String meaning;
    private List<CharStageDTO> stages;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CharStageDTO {
        private String name;
        private String era;
        private String description;
        /** SVG path d 属性，viewBox="0 0 60 90"（内置 30 字用此字段） */
        private String svgPath;
        /** 完整 SVG XML（hanziyuan 抓取的真实字源数据用此字段，前端优先用此渲染） */
        private String svgXml;
    }
}
