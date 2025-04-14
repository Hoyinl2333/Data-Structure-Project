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
        // TODO: 实现topK测试
    }
}
