package com.history.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 地图区域 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MapRegionDTO {
    private String id;
    private String name;
    private String path; // SVG path 数据
    private double centerX; // 中心点经度
    private double centerY; // 中心点纬度
    private String[] aliases;
}
