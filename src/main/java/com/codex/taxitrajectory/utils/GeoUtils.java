package com.codex.taxitrajectory.utils;

import com.codex.taxitrajectory.model.GPSPoint;

import java.util.List;
import java.util.stream.Collectors;

/**
 *  这个类用以实现一些地图中常用的辅助函数
 */

public class GeoUtils {
    /**
     * 判断给定的经纬度是否在矩形区域内。
     * @param longitude 待判断的经度
     * @param latitude 待判断的纬度
     * @param topLeftLongitude 矩形左上角的经度
     * @param topLeftLatitude 矩形左上角的纬度
     * @param bottomRightLongitude 矩形右下角的经度
     * @param bottomRightLatitude 矩形右下角的纬度
     * @return 如果在矩形区域内返回 true，否则返回 false
     */
    public static boolean isInRectangle(double longitude, double latitude,
                                        double topLeftLongitude, double topLeftLatitude,
                                        double bottomRightLongitude, double bottomRightLatitude) {
        return longitude >= bottomRightLongitude && longitude <= topLeftLongitude
                && latitude >= bottomRightLatitude && latitude <= topLeftLatitude;
    }

    /**
     * 将路径转换为字符串表示，例如 "lat1,lon1 -> lat2,lon2"
     * @param path 轨迹点列表
     * @return 路径字符串
     */
    public static String pathToString(List<GPSPoint> path) {
        return path.stream().map(p -> p.getLatitude() + "," + p.getLongitude()).collect(Collectors.joining(" -> "));
    }

    /**
     * 计算两个 GPS 点之间的地理距离（单位：米）
     * @param p1 第一个点
     * @param p2 第二个点
     * @return 距离（米）
     */
    public static double distance(GPSPoint p1, GPSPoint p2) {
        double R = 6371e3; // 地球半径（米）
        double lat1 = Math.toRadians(p1.getLatitude());
        double lat2 = Math.toRadians(p2.getLatitude());
        double deltaLat = Math.toRadians(p2.getLatitude() - p1.getLatitude());
        double deltaLon = Math.toRadians(p2.getLongitude() - p1.getLongitude());

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }
}
