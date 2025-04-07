package com.codex.taxitrajectory.repository;

import com.codex.taxitrajectory.utils.TaxiDataLoadException;
import com.codex.taxitrajectory.model.TaxiRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TxtDataLoader 负责加载出租车轨迹数据，并提供查询接口。
 * 采用按需加载方式，仅在查询时读取特定出租车的轨迹数据。
 * 使用 TreeMap 存储时间戳排序的数据，以支持高效的时间范围查询。
 */
@Slf4j
@Component // 添加该注解，让 Spring 管理此类
public class DataLoader {
    private static final String DATA_FOLDER = "src/main/resources/data/";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 缓存已加载的出租车轨迹数据（出租车 ID -> 时间戳排序的轨迹数据）
    private final Map<String, NavigableMap<LocalDateTime, TaxiRecord>> taxiDataCache = new ConcurrentHashMap<>();

    // 出租车 ID 到数据文件的索引映射
    private final Map<String, File> taxiFileIndex = new HashMap<>();

    public DataLoader() {
        indexDataFiles();
    }

    /**
     * 预扫描 data 目录，建立出租车 ID 到文件的映射索引。
     */
    private void indexDataFiles() {
        File folder = new File(DATA_FOLDER);
        if (!folder.exists() || !folder.isDirectory()) {
            log.error("Data folder not found: " + DATA_FOLDER);
            return;
        }

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".txt"));
        if (files == null) {
            log.error("No data files found in: " + DATA_FOLDER);
            return;
        }

        for (File file : files) {
            String taxiId = file.getName().replace(".txt", "");
            taxiFileIndex.put(taxiId, file);
        }
        log.info("Indexed {} taxi data files.", taxiFileIndex.size());
    }

    /**
     * 加载指定出租车 ID 的轨迹数据到缓存。
     * @param taxiId 出租车 ID
     * @return 时间排序的轨迹数据映射
     */
    private NavigableMap<LocalDateTime, TaxiRecord> loadTaxiData(String taxiId) {
        File file = taxiFileIndex.get(taxiId);
        NavigableMap<LocalDateTime, TaxiRecord> records = new TreeMap<>();

        if (file == null) {
            log.warn("No data file found for taxi ID: {}", taxiId);
            return records;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 4) continue;

                LocalDateTime timestamp = LocalDateTime.parse(parts[1], FORMATTER);
                double longitude = Double.parseDouble(parts[2]);
                double latitude = Double.parseDouble(parts[3]);

                //去除经纬度为0的数据
                if (longitude != 0 && latitude != 0) {
                    records.put(timestamp, new TaxiRecord(taxiId, timestamp, longitude, latitude));
                }

            }
            log.info("Loaded {} records for taxi ID: {}", records.size(), taxiId);
        } catch (IOException e) {
            throw new TaxiDataLoadException("Error reading file: " + file.getName(), e);
        }

        return records;
    }

    /**
     * 获取指定出租车 ID 的时间排序的轨迹数据映射
     * @param taxiId 出租车 ID
     * @return 时间排序的轨迹数据映射
     */
    private NavigableMap<LocalDateTime, TaxiRecord> getTaxiData(String taxiId) {
        return taxiDataCache.computeIfAbsent(taxiId, this::loadTaxiData);
    }

    /**
     * 获取指定出租车的所有轨迹数据。
     * @param taxiId 出租车 ID
     * @return 该出租车的轨迹数据列表
     */
    public List<TaxiRecord> getRecordsByTaxiId(String taxiId) {
        return new ArrayList<>(getTaxiData(taxiId).values());
    }

    /**
     * 按时间范围查询指定出租车的轨迹数据。
     * @param taxiId 出租车 ID
     * @param start 开始时间
     * @param end 结束时间
     * @return 指定时间范围内的轨迹数据列表
     */
    public List<TaxiRecord> getRecordsByTimeRange(String taxiId, LocalDateTime start, LocalDateTime end) {
        return new ArrayList<>(getTaxiData(taxiId).subMap(start, true, end, true).values());
    }

    /**
     * 获取所有出租车 ID
     * @return 所有出租车 ID 的集合
     */
    public Collection<String> getAllTaxiIds() {
        return taxiDataCache.keySet();
    }
}