package com.history.controller;

import com.history.dto.DynastyMapDTO;
import com.history.dto.MapRegionDTO;
import com.history.service.MapDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 历史地图 API 控制器
 */
@RestController
@RequestMapping("/api/v1/map")
@RequiredArgsConstructor
@Tag(name = "历史地图", description = "朝代疆域数据接口")
public class MapController {

    private final MapDataService mapDataService;

    @GetMapping("/regions")
    @Operation(summary = "获取所有地图区域")
    public ResponseEntity<List<MapRegionDTO>> getRegions() {
        return ResponseEntity.ok(mapDataService.getAllRegions());
    }

    @GetMapping("/dynasties")
    @Operation(summary = "获取所有朝代地图数据")
    public ResponseEntity<List<DynastyMapDTO>> getDynastyMaps() {
        return ResponseEntity.ok(mapDataService.getAllDynastyMaps());
    }

    @GetMapping("/dynasty/{name}")
    @Operation(summary = "获取指定朝代的地图数据")
    public ResponseEntity<DynastyMapDTO> getDynastyMap(@PathVariable String name) {
        DynastyMapDTO data = mapDataService.getDynastyMap(name);
        if (data == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(data);
    }
}
