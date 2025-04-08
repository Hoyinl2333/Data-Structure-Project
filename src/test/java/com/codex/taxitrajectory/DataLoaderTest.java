package com.codex.taxitrajectory;

import com.codex.taxitrajectory.model.TaxiRecord;
import com.codex.taxitrajectory.repository.DataLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class DataLoaderTest {

    private DataLoader dataLoader;
    private List<String> taxiIds; // 一系列taxiId
    private static final int LOOKUP_COUNT = 1000; // 模拟大量查找的次数

    @BeforeEach
    void setUp() {
        dataLoader = new DataLoader();
         taxiIds = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            taxiIds.add(String.valueOf(i));
        }
    }

    @Test
    void testGetRecordsByTimeRange() {
        String taxiId = "5632";
        LocalDateTime start = LocalDateTime.of(2008, 2, 2, 0, 0);
        LocalDateTime end = LocalDateTime.of(2008, 2, 9, 0, 0);

        long startTime = System.nanoTime();
        List<TaxiRecord> records = dataLoader.getRecordsByTimeRange(taxiId, start, end);
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000;
        System.out.println("查找时间: " + duration + " 毫秒");
        assertNotNull(records);

        List<TaxiRecord> outOfRangeRecords = records.stream()
                .filter(record -> record.getTimestamp().isBefore(start) || record.getTimestamp().isAfter(end))
                .toList();

        assertEquals(0, outOfRangeRecords.size());
    }

    @Test
    void testGetRecordsOutOfTimeRange() {
        String taxiId = "1";
        LocalDateTime start = LocalDateTime.of(2009, 2, 2, 0, 0);
        LocalDateTime end = LocalDateTime.of(2010, 2, 9, 0, 0);

        List<TaxiRecord> records = dataLoader.getRecordsByTimeRange(taxiId, start, end);

        assertEquals(0, records.size());
    }

    @Test
    void testGetTopFiveRecordsByTaxiId() {
        String taxiId = "1111";
        long startTime = System.nanoTime();
        List<TaxiRecord> records = dataLoader.getRecordsByTaxiId(taxiId);
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000;
        System.out.println("查找时间: " + duration + " 毫秒");

        List<TaxiRecord> topFiveRecords = records.stream()
                .limit(5)
                .toList();

        System.out.println("前五个记录:");
        topFiveRecords.forEach(record -> System.out.println(record.getTaxiId() + ", " + record.getTimestamp() + ", " + record.getLongitude() + ", " + record.getLatitude()));

        assertTrue(topFiveRecords.size() <= 5);
    }

    @Test
    void testGetRecordsBySpecificTimeRange() {
        String taxiId = "5632";
        LocalDateTime start = LocalDateTime.of(2008, 2, 2, 0, 0);
        LocalDateTime end = LocalDateTime.of(2008, 2, 8, 0, 0);

        long startTimeNano = System.nanoTime();
        List<TaxiRecord> records = dataLoader.getRecordsByTimeRange(taxiId, start, end);
        long endTimeNano = System.nanoTime();
        long duration = (endTimeNano - startTimeNano) / 1000000;
        System.out.println("查找时间: " + duration + " 毫秒");
        assertNotNull(records);

        List<TaxiRecord> outOfRangeRecords = records.stream()
                .filter(record -> record.getTimestamp().isBefore(start) || record.getTimestamp().isAfter(end))
                .toList();

        assertEquals(0, outOfRangeRecords.size());
    }

    @Test
    void testGetRecordsInBeijing() {
        String taxiId = "5632";
        // 北京大致的经纬度范围
        double minLongitude = 115.41;
        double maxLongitude = 117.5;
        double minLatitude = 39.44;
        double maxLatitude = 41.06;

        long startTime = System.nanoTime();
        List<TaxiRecord> records = dataLoader.getRecordsByTaxiId(taxiId);
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000;
        System.out.println("查找时间: " + duration + " 毫秒");

        List<TaxiRecord> beijingRecords = records.stream()
                .filter(record -> record.getLongitude() >= minLongitude && record.getLongitude() <= maxLongitude
                        && record.getLatitude() >= minLatitude && record.getLatitude() <= maxLatitude)
                .toList();

        System.out.println("在北京范围内的记录数量: " + beijingRecords.size());
        assertNotNull(beijingRecords);
    }


    @Test
    void testLargeScaleLookupByIndex() {
        Random random = new Random();
        long startTime = System.nanoTime();

        for (int i = 0; i < LOOKUP_COUNT; i++) {
            // 随机选择一个出租车 ID 进行查找
            String taxiId = taxiIds.get(random.nextInt(taxiIds.size()));
            List<TaxiRecord> records = dataLoader.getRecordsByTaxiId(taxiId);
            assertNotNull(records);
        }

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000; // 转换为毫秒
        System.out.println("进行 " + LOOKUP_COUNT + " 次查找操作，总共耗时: " + duration + " 毫秒");
    }

    @Test
    void testLargeScaleLookupByTimeRange() {
        Random random = new Random();
        long startTime = System.nanoTime();

        LocalDateTime start = LocalDateTime.of(2008, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2010, 1, 1, 0, 0);

        for (int i = 0; i < LOOKUP_COUNT; i++) {
            // 随机选择一个出租车 ID 进行时间范围查找
            String taxiId = taxiIds.get(random.nextInt(taxiIds.size()));
            List<TaxiRecord> records = dataLoader.getRecordsByTimeRange(taxiId, start, end);
            assertNotNull(records);
        }

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000; // 转换为毫秒
        System.out.println("进行 " + LOOKUP_COUNT + " 次时间范围查找操作，总共耗时: " + duration + " 毫秒");
    }

    @Test //用来单独查询某个ID的全部记录，检查清洗数据结果
    void testQueryAllRecordsForTaxi() {

        String taxiId = "9757";


        List<TaxiRecord> records = dataLoader.getRecordsByTaxiId(taxiId);

        if (records.isEmpty()) {
            System.out.println("未找到出租车 ID 为 " + taxiId + " 的记录。");
        } else {
            System.out.println("找到出租车 ID 为 " + taxiId + " 的记录，共 " + records.size() + " 条：");
            for (TaxiRecord record : records) {

                System.out.println("Taxi ID: " + record.getTaxiId() +
                        ", Timestamp: " + record.getTimestamp() +

                        ", Longitude: " + record.getLongitude() +
                        ", Latitude: " + record.getLatitude());
            }
        }
    }

    @Test //测试查询一定时间范围内的出租出记录
    void testQueryTimeRangeRecordsForTaxi() {
        String taxiId = "1";
        LocalDateTime start = LocalDateTime.of(2008, 2, 2, 0, 0);
        LocalDateTime end = LocalDateTime.of(2008, 2, 2, 18, 0);
        List<TaxiRecord> records = dataLoader.getRecordsByTimeRange(taxiId, start, end);

        if (records.isEmpty()) {
            System.out.println("未找到出租车 ID 为 " + taxiId + " 的记录。");
        } else {
            System.out.println("找到出租车 ID 为 " + taxiId + " 的记录，共 " + records.size() + " 条：");
            for (TaxiRecord record : records) {
                System.out.println("Taxi ID: " + record.getTaxiId() +
                        ", Timestamp: " + record.getTimestamp() +
                        ", Longitude: " + record.getLongitude() +
                        ", Latitude: " + record.getLatitude());
            }
        }
    }
}