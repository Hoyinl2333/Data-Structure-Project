package com.codex.taxitrajectory.model;

import lombok.Data;

@Data
public class GPSPoint {
    private double latitude;
    private double longitude;

    public GPSPoint(double latitude, double latitude1) {
    }

    /**
     * 从 TaxiRecord 创建 GPSPoint
     * @param record 出租车轨迹记录
     * @return GPSPoint 对象
     */
    public static GPSPoint fromTaxiRecord(TaxiRecord record) {
        return new GPSPoint(record.getLatitude(), record.getLongitude());
    }
}
