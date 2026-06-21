package com.history.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 朝代数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DynastyDTO {
    private Long id;
    private String uid;
    private String name;
    private String fullName;
    private String period;
    private Integer periodStart;
    private Integer periodEnd;
    private String founder;
    private String lastRuler;
    private String capital;
    private String duration;
    private String highlights;
    private String description;
    private String fallReason;
    private String legacy;
}
