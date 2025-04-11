package com.codex.taxitrajectory.service;

import com.codex.taxitrajectory.model.GPSPoint;
import com.codex.taxitrajectory.model.TaxiRecord;
import com.codex.taxitrajectory.repository.DataLoader;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;
import com.codex.taxitrajectory.utils.GeoUtils;
/**
 * 路径分析服务类，处理路径相关的统计与计算功能。
 * 涵盖功能：
 * - F7 频繁路径分析1：统计最常见的前k条路径。
 * - F8 频繁路径分析2：统计从区域A到B最常见的前k条路径。
 * - F9 通行时间分析：计算A到B的最短通行路径及相应时间。
 */


//TODO: F7.频繁路径分析1

public class PathAnalysisService {

    /**
     * 存储路径及其频次的对象类
     */
    @Data
    public static class PathFrequency {
        private final String path; // 路径字符串
        private final int frequency; // 通过该路径的车辆数量

        public PathFrequency(String path, int frequency) {
            this.path = path;
            this.frequency = frequency;
        }
    }


    private final DataLoader dataLoader;
    private double DISTANCE_THRESHOLD; // parameter x
    private int TOP_K; // parameter k

    public PathAnalysisService(DataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }

    /**
     * 获取最常见的前 K 条路径
     * @return 频繁路径列表
     */
    public List<PathFrequency> getTopKFrequentPaths(double distanceThreshold, int topK) {
        DISTANCE_THRESHOLD = distanceThreshold;
        TOP_K = topK;
        Map<String, Set<String>> pathToCarMap = new HashMap<>();

        for (String taxiId : dataLoader.getAllTaxiIds()) {
            // 逐个获取出租车轨迹，避免一次性加载所有数据
            List<TaxiRecord> records = dataLoader.getRecordsByTaxiId(taxiId);

            List<String> paths = extractPaths(records);
            for (String path : paths) {
                pathToCarMap.putIfAbsent(path, new HashSet<>());
                pathToCarMap.get(path).add(taxiId);
            }
        }

        // 统计路径的车辆数，并排序取前 K 个
        return pathToCarMap.entrySet().stream()
                .map(e -> new PathFrequency(e.getKey(), e.getValue().size()))
                .sorted(Comparator.comparingInt(PathFrequency::getFrequency).reversed())
                .limit(TOP_K)
                .collect(Collectors.toList());
    }

    /**
     * 从单辆车的轨迹中提取路径
     * @param trajectory 车辆轨迹点列表
     * @return 该车辆行驶过的路径列表
     */
    private List<String> extractPaths(List<TaxiRecord> trajectory) {
        if (trajectory.size() < 2) return new ArrayList<>();

        List<String> paths = new ArrayList<>();
        List<GPSPoint> currentPath = new ArrayList<>();

        for (int i = 0; i < trajectory.size() - 1; i++) {
            GPSPoint p1 = GPSPoint.fromTaxiRecord(trajectory.get(i));
            GPSPoint p2 = GPSPoint.fromTaxiRecord(trajectory.get(i + 1));

            if (GeoUtils.distance(p1, p2) > DISTANCE_THRESHOLD) {
                if (currentPath.size() > 1) { // 确保至少包含两个点
                    paths.add(GeoUtils.pathToString(currentPath));
                }
                currentPath.clear();
                currentPath.add(p2); // 避免路径断点丢失
            }
            currentPath.add(p1);
        }

        if (currentPath.size() > 1) {
            paths.add(GeoUtils.pathToString(currentPath));
        }
        return paths;
    }
    //TODO: F8.频繁路径分析

    //TODO: F9.通行时间分析
}
