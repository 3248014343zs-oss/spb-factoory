package com.factory.service;

import com.factory.vo.InventoryTrendVO;
import com.factory.vo.StockStatsVO;
import java.util.List;

public interface ReportService {
    List<InventoryTrendVO> getInventoryTrend(Long materialId, int days);
    StockStatsVO getStockStats(String startTime, String endTime, Long materialId);
    List<StockStatsVO> getStockStatsList(String startTime, String endTime, Long materialId);
}