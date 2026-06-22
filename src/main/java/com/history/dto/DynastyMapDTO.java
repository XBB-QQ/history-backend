package com.history.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * 朝代地图数据 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DynastyMapDTO {
    private String dynastyName;
    private List<String> regionIds;
    private String period;
    private double capitalLng;
    private double capitalLat;
    private String capitalName;
}
