package com.codex.taxitrajectory;

import com.codex.taxitrajectory.model.GPSPoint;
import com.codex.taxitrajectory.model.TaxiRecord;
import com.codex.taxitrajectory.repository.DataLoader;
import com.codex.taxitrajectory.service.PathAnalysisService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class FrequentPathServiceTest {
    private DataLoader dataLoader;
    private PathAnalysisService frequentPathService;

    @BeforeEach
    void setUp() {
        dataLoader = mock(DataLoader.class);  // 使用 Mockito 创建 DataLoader 的模拟对象
        frequentPathService = new PathAnalysisService(dataLoader);
    }

    @Test
    void testGetTopKFrequentPaths() {
        // 1. 创建模拟数据（List<TaxiRecord>）
        List<TaxiRecord> mockData = new ArrayList<>();

        mockData.add(new TaxiRecord("1", LocalDateTime.of(2025, 3, 30, 12, 0), 116.51172, 39.92123));
        mockData.add(new TaxiRecord("1", LocalDateTime.of(2025, 3, 30, 12, 5), 116.51135, 39.93883));

        mockData.add(new TaxiRecord("2", LocalDateTime.of(2025, 3, 30, 12, 2), 116.51172, 39.92123));
        mockData.add(new TaxiRecord("2", LocalDateTime.of(2025, 3, 30, 12, 7), 116.51627, 39.91034));

        mockData.add(new TaxiRecord("3", LocalDateTime.of(2025, 3, 30, 12, 10), 116.51135, 39.93883));
        mockData.add(new TaxiRecord("3", LocalDateTime.of(2025, 3, 30, 12, 15), 116.51627, 39.91034));

        List<String> mockTaxiIds = mockData.stream()
                .map(TaxiRecord::getTaxiId)
                .distinct()  // 确保 ID 唯一
                .collect(Collectors.toList());

        // 2. 让 mock 返回模拟数据
        when(dataLoader.getAllTaxiRecords()).thenReturn(mockData);
        when(dataLoader.getAllTaxiIds()).thenReturn(mockTaxiIds);
        when(dataLoader.getRecordsByTaxiId(anyString())).thenAnswer(invocation -> {
            String taxiId = invocation.getArgument(0);
            return mockData.stream()
                    .filter(record -> taxiId.equals(record.getTaxiId()))
                    .collect(Collectors.toList());
        });

        // 3. 测试模拟数据
        List<TaxiRecord> result1 = dataLoader.getRecordsByTaxiId("1");
        List<TaxiRecord> result2 = dataLoader.getRecordsByTaxiId("2");

        assertNotNull(result1);
        assertEquals(2, result1.size());
        assertEquals("1", result1.get(0).getTaxiId());  // Java 8+ 兼容
        assertEquals("2", result2.get(0).getTaxiId());

        // 4. 调用测试方法
        List<PathAnalysisService.PathFrequency> result = frequentPathService.getTopKFrequentPaths( 50.0, 3);

        // 5. 验证路径数据
        assertEquals(3, result.size()); // 取前 3 个路径
        assertEquals("39.92123,116.51172 -> 39.93883,116.51135", result.get(0).getPath());
        assertEquals("39.92123,116.51172 -> 39.91034,116.51627", result.get(1).getPath());
        assertEquals("39.93883,116.51135 -> 39.91034,116.51627", result.get(2).getPath());

        // 6. 确保 DataLoader 只调用了一次
        verify(dataLoader, times(1)).getAllTaxiRecords();

    }
}
