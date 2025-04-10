package com.codex.taxitrajectory.service;


import com.codex.taxitrajectory.model.TaxiRecord;

import com.codex.taxitrajectory.model.GridCell;
import com.codex.taxitrajectory.repository.DataLoader;
import com.codex.taxitrajectory.utils.GeoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
@Slf4j

@Component
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


    /**
     * F4. 区域车流密度分析。给定距离参数r,将整个地图划分成网格，每个格子的大小是r*r。
     * 统计分析在不同的时间段，经过所有网格区域内的车流密度的变化。
     * @param start 开始时间
     * @param end 结束时间
     * @param r 网格大小
     * @return 每个网格的车流密度统计结果
     */
    public Map<GridCell, Integer> analyzeTrafficDensity(LocalDateTime start, LocalDateTime end, double r) {
        // 获取所有出租车ID
        Collection<String> allTaxiIds = dataLoader.getAllTaxiIds();
        // 用于存储每个网格的车流密度
        Map<GridCell, Integer> gridCellDensity = new HashMap<>();

        // 遍历所有出租车
        for (String taxiId : allTaxiIds) {
            // 获取指定出租车在特定时间段内的轨迹数据
            List<TaxiRecord> records = dataLoader.getRecordsByTimeRange(taxiId, start, end);
            for (TaxiRecord record : records) {
                double longitude = record.getLongitude();
                double latitude = record.getLatitude();
                // 计算该记录所在的网格
                GridCell gridCell = getGridCell(longitude, latitude, r);
                // 检查网格是否已经存在于 map 中
                gridCellDensity.put(gridCell, gridCellDensity.getOrDefault(gridCell, 0) + 1);
            }
        }
        return gridCellDensity;
    }

    /**
     * 根据经纬度和网格大小计算所在的网格
     * @param longitude 经度
     * @param latitude 纬度
     * @param r 网格大小
     * @return 所在的网格
     */
    private GridCell getGridCell(double longitude, double latitude, double r) {
        int row = (int) (latitude / r);
        int col = (int) (longitude / r);
        GridCell gridCell = new GridCell();
        gridCell.setRow(row);
        gridCell.setCol(col);
        return gridCell;
    }

    /**
     * F5. 区域关联分析1：分别统计两个指定区域间不同方向的车流量变化。
     * @param start 开始时间
     * @param end 结束时间
     * @param topLeftLongitude1 第一个矩形区域左上角的经度
     * @param topLeftLatitude1 第一个矩形区域左上角的纬度
     * @param bottomRightLongitude1 第一个矩形区域右下角的经度
     * @param bottomRightLatitude1 第一个矩形区域右下角的纬度
     * @param topLeftLongitude2 第二个矩形区域左上角的经度
     * @param topLeftLatitude2 第二个矩形区域左上角的纬度
     * @param bottomRightLongitude2 第二个矩形区域右下角的经度
     * @param bottomRightLatitude2 第二个矩形区域右下角的纬度
     * @return 包含两个方向车流量的数组，第一个元素是区域1到区域2的车流量，第二个元素是区域2到区域1的车流量
     */
    public int[] analyzeTrafficFlowBetweenRegions(LocalDateTime start, LocalDateTime end,
                                                  double topLeftLongitude1, double topLeftLatitude1,
                                                  double bottomRightLongitude1, double bottomRightLatitude1,
                                                  double topLeftLongitude2, double topLeftLatitude2,
                                                  double bottomRightLongitude2, double bottomRightLatitude2) {
        Collection<String> allTaxiIds = dataLoader.getAllTaxiIds();
        int flowFromRegion1ToRegion2 = 0;
        int flowFromRegion2ToRegion1 = 0;

        for (String taxiId : allTaxiIds) {
            List<TaxiRecord> records = dataLoader.getRecordsByTimeRange(taxiId, start, end);
            boolean inRegion1 = false;
            boolean inRegion2 = false;
            boolean firstInRegion1 = false;

            for (TaxiRecord record : records) {
                double longitude = record.getLongitude();
                double latitude = record.getLatitude();

                boolean isInRegion1 = GeoUtils.isInRectangle(longitude, latitude, topLeftLongitude1, topLeftLatitude1, bottomRightLongitude1, bottomRightLatitude1);
                boolean isInRegion2 = GeoUtils.isInRectangle(longitude, latitude, topLeftLongitude2, topLeftLatitude2, bottomRightLongitude2, bottomRightLatitude2);

                if (isInRegion1 && !inRegion1) {
                    inRegion1 = true;
                    if (!inRegion2) {
                        firstInRegion1 = true;
                    }
                }
                if (isInRegion2 && !inRegion2) {
                    inRegion2 = true;
                    if (!inRegion1) {
                        firstInRegion1 = false;
                    }
                }

                if (inRegion1 && inRegion2) {
                    if (firstInRegion1) {
                        flowFromRegion1ToRegion2++;
                    } else {
                        flowFromRegion2ToRegion1++;
                    }
                    break;
                }
            }
        }

        return new int[]{flowFromRegion1ToRegion2, flowFromRegion2ToRegion1};
    }

    //TODO: F5.区域关连分析1


    // TODO: F6.区域关联分析2
}