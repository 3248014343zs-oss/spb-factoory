package com.factory.service;

import com.factory.vo.DashboardStatsVO;
import com.factory.vo.InventoryTrendVO;
import com.factory.vo.MaterialPieVO;
import java.util.List;

public interface DashboardService {
    DashboardStatsVO getStats();
    List<InventoryTrendVO> getInventoryTrend(int days);
    List<MaterialPieVO> getMaterialPie();
}