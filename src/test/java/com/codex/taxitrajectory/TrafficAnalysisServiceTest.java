package com.codex.taxitrajectory;
import com.codex.taxitrajectory.model.TaxiRecord;
import com.codex.taxitrajectory.repository.DataLoader;
import com.codex.taxitrajectory.service.TrafficAnalysisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class TrafficAnalysisServiceTest {
    private DataLoader mockDataLoader;
    private TrafficAnalysisService trafficAnalysisService;

    @BeforeEach
    public void setUp() {
        mockDataLoader = Mockito.mock(DataLoader.class);
        trafficAnalysisService = new TrafficAnalysisService(mockDataLoader);
    }


  /////////////////////////////////////// F3 单元测试代码 ///////////////////////////////////////

    // TODO: 完善查询，添加日志

    @Test
    public void testCountTaxisInRegion_AllTaxisInside() {
        LocalDateTime start = LocalDateTime.of(2008, 2, 6, 0, 0);
        LocalDateTime end = LocalDateTime.of(2008, 2, 6, 23, 59);
        double topLeftLongitude = 116.6;
        double topLeftLatitude = 40.1;
        double bottomRightLongitude = 116.2;
        double bottomRightLatitude = 39.8;

        Collection<String> taxiIds = Arrays.asList("taxi1", "taxi2");
        when(mockDataLoader.getAllTaxiIds()).thenReturn(taxiIds);

        List<TaxiRecord> records1 = new ArrayList<>();
        records1.add(new TaxiRecord("taxi1", start, 116.4, 39.9));
        when(mockDataLoader.getRecordsByTimeRange("taxi1", start, end)).thenReturn(records1);

        List<TaxiRecord> records2 = new ArrayList<>();
        records2.add(new TaxiRecord("taxi2", start, 116.5, 40.0));
        when(mockDataLoader.getRecordsByTimeRange("taxi2", start, end)).thenReturn(records2);

        long startTime = System.nanoTime();

        int result = trafficAnalysisService.countTaxisInRegion(start, end, topLeftLongitude, topLeftLatitude, bottomRightLongitude, bottomRightLatitude);

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000; // 转换为毫秒
        System.out.println("执行一次countTaxisInRegion,总共耗时: " + duration + " 毫秒");

        assertEquals(2, result);
    }

    @Test
    public void testCountTaxisInRegion_NoTaxisInside() {
        LocalDateTime start = LocalDateTime.of(2008, 2, 6, 0, 0);
        LocalDateTime end = LocalDateTime.of(2008, 2, 6, 23, 59);
        double topLeftLongitude = 116.6;
        double topLeftLatitude = 40.1;
        double bottomRightLongitude = 116.2;
        double bottomRightLatitude = 39.8;

        Collection<String> taxiIds = Arrays.asList("taxi1", "taxi2");
        when(mockDataLoader.getAllTaxiIds()).thenReturn(taxiIds);

        List<TaxiRecord> records1 = new ArrayList<>();
        records1.add(new TaxiRecord("taxi1", start, 115.5, 39.0));
        when(mockDataLoader.getRecordsByTimeRange("taxi1", start, end)).thenReturn(records1);

        List<TaxiRecord> records2 = new ArrayList<>();
        records2.add(new TaxiRecord("taxi2", start, 115.6, 39.1));
        when(mockDataLoader.getRecordsByTimeRange("taxi2", start, end)).thenReturn(records2);

        int result = trafficAnalysisService.countTaxisInRegion(start, end, topLeftLongitude, topLeftLatitude, bottomRightLongitude, bottomRightLatitude);
        assertEquals(0, result);
    }

    @Test
    public void testCountTaxisInRegion_SomeTaxisInside() {
        LocalDateTime start = LocalDateTime.of(2008, 2, 6, 0, 0);
        LocalDateTime end = LocalDateTime.of(2008, 2, 6, 23, 59);
        double topLeftLongitude = 116.6;
        double topLeftLatitude = 40.1;
        double bottomRightLongitude = 116.2;
        double bottomRightLatitude = 39.8;

        Collection<String> taxiIds = Arrays.asList("taxi1", "taxi2");
        when(mockDataLoader.getAllTaxiIds()).thenReturn(taxiIds);

        List<TaxiRecord> records1 = new ArrayList<>();
        records1.add(new TaxiRecord("taxi1", start, 116.4, 39.9));
        when(mockDataLoader.getRecordsByTimeRange("taxi1", start, end)).thenReturn(records1);

        List<TaxiRecord> records2 = new ArrayList<>();
        records2.add(new TaxiRecord("taxi2", start, 115.5, 39.0));
        when(mockDataLoader.getRecordsByTimeRange("taxi2", start, end)).thenReturn(records2);

        int result = trafficAnalysisService.countTaxisInRegion(start, end, topLeftLongitude, topLeftLatitude, bottomRightLongitude, bottomRightLatitude);
        assertEquals(1, result);
    }

    @Test
    public void testCountTaxisInRegion_NoRecords() {
        LocalDateTime start = LocalDateTime.of(2008, 2, 6, 0, 0);
        LocalDateTime end = LocalDateTime.of(2008, 2, 6, 23, 59);
        double topLeftLongitude = 116.6;
        double topLeftLatitude = 40.1;
        double bottomRightLongitude = 116.2;
        double bottomRightLatitude = 39.8;

        Collection<String> taxiIds = Collections.emptyList();
        when(mockDataLoader.getAllTaxiIds()).thenReturn(taxiIds);

        int result = trafficAnalysisService.countTaxisInRegion(start, end, topLeftLongitude, topLeftLatitude, bottomRightLongitude, bottomRightLatitude);
        assertEquals(0, result);
    }


    /////////////////////////////////////////////////////////////////////////////////////////
}
