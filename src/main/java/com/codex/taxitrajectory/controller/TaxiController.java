package com.codex.taxitrajectory.controller;

import com.codex.taxitrajectory.model.GridCell;
import com.codex.taxitrajectory.model.TaxiRecord;
import com.codex.taxitrajectory.repository.DataLoader;
import com.codex.taxitrajectory.service.TrafficAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/taxi")
public class TaxiController {

    private final DataLoader dataLoader;
    private final TrafficAnalysisService trafficAnalysisService;

    public TaxiController(DataLoader dataLoader, TrafficAnalysisService trafficAnalysisService) {
        this.dataLoader = dataLoader;
        this.trafficAnalysisService = trafficAnalysisService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<TaxiRecord>> getTaxiTrajectory(@PathVariable String id) {
        List<TaxiRecord> records = dataLoader.getRecordsByTaxiId(id);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/countInRegion")
    public ResponseEntity<Integer> countTaxisInRegion(
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam double topLeftLongitude,
            @RequestParam double topLeftLatitude,
            @RequestParam double bottomRightLongitude,
            @RequestParam double bottomRightLatitude) {

        // 修改日期时间格式化器
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime start = LocalDateTime.parse(startTime, formatter);
        LocalDateTime end = LocalDateTime.parse(endTime, formatter);

        int taxiCount = trafficAnalysisService.countTaxisInRegion(
                start, end, topLeftLongitude, topLeftLatitude, bottomRightLongitude, bottomRightLatitude);
        System.out.println(taxiCount);
        return ResponseEntity.ok(taxiCount);
    }

    @GetMapping("/densityAnalysis")
    public ResponseEntity<Map<GridCell, Integer>> densityAnalysis(
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam double topLeftLongitude,
            @RequestParam double topLeftLatitude,
            @RequestParam double bottomRightLongitude,
            @RequestParam double bottomRightLatitude,
            @RequestParam double gridRadius) {

        // 修改日期时间格式化器
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime start = LocalDateTime.parse(startTime, formatter);
        LocalDateTime end = LocalDateTime.parse(endTime, formatter);

        Map<GridCell, Integer> densityResult = trafficAnalysisService.analyzeTrafficDensity(
                start, end, gridRadius);

        return ResponseEntity.ok(densityResult);
    }
}