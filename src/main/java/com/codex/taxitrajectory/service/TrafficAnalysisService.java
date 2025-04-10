package com.codex.taxitrajectory.service;


import com.codex.taxitrajectory.model.TaxiRecord;
import com.codex.taxitrajectory.repository.DataLoader;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static com.codex.taxitrajectory.utils.GeoUtils.isInRectangle;

/**
 * 车流与区域分析服务类，负责区域统计、车流密度及关联分析功能。
 * 涵盖功能：
 * - F3 区域范围查找：统计特定时间段内矩形区域的出租车数目。
 * - F4 车流密度分析：基于网格（r*r）分析车流密度变化。
 * - F5 区域关联分析1：统计两个指定区域间的车流量变化。
 * - F6 区域关联分析2：统计某个区域与其他区域的车流变化。
 */
public class TrafficAnalysisService {

    private final DataLoader dataLoader;

    public TrafficAnalysisService(DataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }


    // TODO: F3.区域范围查找

    /**
     * F3. 区域范围查找：统计特定时间段内矩形区域的出租车数目。
     * @param start 开始时间
     * @param end 结束时间
     * @param topLeftLongitude 矩形左上角的经度
     * @param topLeftLatitude 矩形左上角的纬度
     * @param bottomRightLongitude 矩形右下角的经度
     * @param bottomRightLatitude 矩形右下角的纬度
     * @return 特定时间段内矩形区域的出租车数目
     */
    public int countTaxisInRegion(LocalDateTime start, LocalDateTime end,
                                  double topLeftLongitude, double topLeftLatitude,
                                  double bottomRightLongitude, double bottomRightLatitude) {

        Collection<String> allTaxiIds = dataLoader.getAllTaxiIds();
        int taxiCount = 0;

        for (String taxiId : allTaxiIds) {
            // 获取指定出租车在特定时间段内的轨迹数据
            List<TaxiRecord> records = dataLoader.getRecordsByTimeRange(taxiId, start, end);
            for (TaxiRecord record : records) {
                double longitude = record.getLongitude();
                double latitude = record.getLatitude();
                // 判断该记录的经纬度是否在矩形区域内
                if (isInRectangle(longitude, latitude, topLeftLongitude, topLeftLatitude,
                        bottomRightLongitude, bottomRightLatitude)) {
                    taxiCount++;
                    break; // 只要该出租车有一条记录在区域内，就计数并跳出内层循环
                }
            }
        }
        return taxiCount;
    }



    // TODO: F4.车流密度分析

    // TODO: F5.区域关联分析1

    // TODO: F6.区域关联分析2
}