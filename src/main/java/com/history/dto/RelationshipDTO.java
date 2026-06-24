package com.history.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 人物关系数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RelationshipDTO {
    /** 目标人物 uid */
    private String targetUid;
    /** 关系类型：师徒/敌对/亲属/朋友 */
    private String relation;
    /** 关系描述 */
    private String label;
}
