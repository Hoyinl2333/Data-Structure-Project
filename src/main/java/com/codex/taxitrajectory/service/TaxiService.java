package com.codex.taxitrajectory.service;

import com.codex.taxitrajectory.model.TaxiRecord;

import java.time.LocalDateTime;
import java.util.List;

public class TaxiService {
    public List<TaxiRecord> getRecordsByTaxiId(String taxiId) {
        return List.of();
    }
    public List<TaxiRecord> getRecordsByTimeRange(LocalDateTime start, LocalDateTime end) {
        return List.of();
    }
}