package com.codex.taxitrajectory.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Data
public class TaxiRecord {
    private String taxiId;
    private LocalDateTime timestamp;
    private double longitude;
    private double latitude;

    public TaxiRecord(String taxiId, LocalDateTime timestamp, double longitude, double latitude) {
        this.taxiId = taxiId;
        this.timestamp = timestamp;
        this.longitude = longitude;
        this.latitude = latitude;
    }

}
