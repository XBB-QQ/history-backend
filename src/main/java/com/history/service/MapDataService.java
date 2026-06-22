package com.history.service;

import com.history.dto.DynastyMapDTO;
import com.history.dto.MapRegionDTO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 地图数据服务
 */
@Service
public class MapDataService {

    private static final List<MapRegionDTO> REGIONS = List.of(
        new MapRegionDTO("xinjiang", "新疆", "", 85, 43, new String[]{"西域", "新疆省"}),
        new MapRegionDTO("tibet", "西藏", "", 86, 32, new String[]{"青藏高原", "吐蕃"}),
        new MapRegionDTO("qinghai", "青海", "", 96, 35, new String[]{"青海省", "西宁"}),
        new MapRegionDTO("gansu", "甘肃", "", 100, 38, new String[]{"凉州", "甘州", "河西走廊"}),
        new MapRegionDTO("inner-mongolia", "内蒙古", "", 112, 44, new String[]{"蒙古", "塞北", "漠南"}),
        new MapRegionDTO("sichuan", "四川", "", 103, 30, new String[]{"蜀", "巴蜀", "川蜀"}),
        new MapRegionDTO("shaanxi", "陕西", "", 108, 35, new String[]{"秦", "关中", "长安"}),
        new MapRegionDTO("shanxi", "山西", "", 112, 38, new String[]{"晋"}),
        new MapRegionDTO("hebei", "河北", "", 116, 39, new String[]{"冀", "直隶"}),
        new MapRegionDTO("henan", "河南", "", 113, 34, new String[]{"豫", "中原"}),
        new MapRegionDTO("shandong", "山东", "", 118, 36, new String[]{"鲁"}),
        new MapRegionDTO("jiangsu", "江苏", "", 119, 33, new String[]{"苏", "江淮"}),
        new MapRegionDTO("zhejiang", "浙江", "", 120, 29, new String[]{"浙", "江浙"}),
        new MapRegionDTO("anhui", "安徽", "", 117, 32, new String[]{"皖"}),
        new MapRegionDTO("hunan", "湖南", "", 112, 28, new String[]{"湘"}),
        new MapRegionDTO("hubei", "湖北", "", 112, 31, new String[]{"鄂"}),
        new MapRegionDTO("jiangxi", "江西", "", 116, 27, new String[]{"赣"}),
        new MapRegionDTO("fujian", "福建", "", 118, 26, new String[]{"闽"}),
        new MapRegionDTO("guangdong", "广东", "", 113, 23, new String[]{"粤", "岭南"}),
        new MapRegionDTO("guangxi", "广西", "", 108, 24, new String[]{"桂"}),
        new MapRegionDTO("guizhou", "贵州", "", 107, 27, new String[]{"黔"}),
        new MapRegionDTO("yunnan", "云南", "", 102, 25, new String[]{"滇"}),
        new MapRegionDTO("liaoning", "辽宁", "", 122, 41, new String[]{"辽", "奉天"}),
        new MapRegionDTO("jilin", "吉林", "", 126, 44, new String[]{"吉"}),
        new MapRegionDTO("heilongjiang", "黑龙江", "", 128, 49, new String[]{"黑"}),
        new MapRegionDTO("taiwan", "台湾", "", 121, 23, new String[]{"台"})
    );

    private static final List<DynastyMapDTO> DATA = List.of(
        new DynastyMapDTO("夏", List.of("henan", "shanxi", "shaanxi"), "约前2070年—约前1600年", 113, 34, "阳城"),
        new DynastyMapDTO("商", List.of("henan", "shandong", "hebei", "anhui"), "约前1600年—前1046年", 114, 35, "殷(安阳)"),
        new DynastyMapDTO("周", List.of("shaanxi", "henan", "shanxi", "hubei", "anhui", "jiangsu"), "前1046年—前256年", 109, 34, "镐京/洛邑"),
        new DynastyMapDTO("秦", List.of("shaanxi", "gansu", "shanxi", "henan", "hubei", "hunan", "sichuan"), "前221年—前207年", 109, 34, "咸阳"),
        new DynastyMapDTO("西汉", List.of("shaanxi", "gansu", "henan", "shanxi", "sichuan", "hebei", "shandong", "hubei", "anhui", "jiangsu", "qinghai"), "前202年—公元8年", 109, 34, "长安"),
        new DynastyMapDTO("东汉", List.of("shaanxi", "henan", "shanxi", "sichuan", "hebei", "shandong", "hubei", "anhui", "jiangsu", "jiangxi", "hunan", "qinghai", "gansu"), "公元25年—220年", 114, 34, "洛阳"),
        new DynastyMapDTO("唐", List.of("shaanxi", "gansu", "xinjiang", "henan", "shanxi", "hebei", "shandong", "sichuan", "hubei", "hunan", "jiangxi", "fujian", "guangdong", "jiangsu", "anhui", "zhejiang"), "618年—907年", 109, 34, "长安"),
        new DynastyMapDTO("宋", List.of("henan", "shanxi", "hebei", "shandong", "sichuan", "hubei", "hunan", "jiangxi", "fujian", "guangdong", "guangxi", "jiangsu", "anhui", "zhejiang", "guizhou"), "960年—1279年", 114, 34, "开封/临安"),
        new DynastyMapDTO("元", List.of("heilongjiang", "jilin", "liaoning", "inner-mongolia", "shaanxi", "gansu", "xinjiang", "qinghai", "tibet", "henan", "shanxi", "hebei", "shandong", "sichuan", "yunnan", "guizhou", "hubei", "hunan", "jiangxi", "fujian", "guangdong", "guangxi", "jiangsu", "anhui", "zhejiang", "taiwan"), "1271年—1368年", 116, 39, "大都"),
        new DynastyMapDTO("明", List.of("heilongjiang", "jilin", "liaoning", "inner-mongolia", "shaanxi", "gansu", "qinghai", "henan", "shanxi", "hebei", "shandong", "sichuan", "yunnan", "guizhou", "hubei", "hunan", "jiangxi", "fujian", "guangdong", "guangxi", "jiangsu", "anhui", "zhejiang", "taiwan"), "1368年—1644年", 116, 39, "南京/北京"),
        new DynastyMapDTO("清", List.of("heilongjiang", "jilin", "liaoning", "inner-mongolia", "xinjiang", "qinghai", "tibet", "shaanxi", "gansu", "henan", "shanxi", "hebei", "shandong", "sichuan", "yunnan", "guizhou", "hubei", "hunan", "jiangxi", "fujian", "guangdong", "guangxi", "jiangsu", "anhui", "zhejiang", "taiwan"), "1644年—1912年", 116, 39, "北京")
    );

    public List<MapRegionDTO> getAllRegions() {
        return REGIONS;
    }

    public List<DynastyMapDTO> getAllDynastyMaps() {
        return DATA;
    }

    public DynastyMapDTO getDynastyMap(String dynastyName) {
        return DATA.stream()
            .filter(d -> d.getDynastyName().equals(dynastyName))
            .findFirst()
            .orElse(null);
    }
}
