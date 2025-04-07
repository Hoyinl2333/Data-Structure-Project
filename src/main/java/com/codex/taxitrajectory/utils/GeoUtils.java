package com.codex.taxitrajectory.utils;

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
}
