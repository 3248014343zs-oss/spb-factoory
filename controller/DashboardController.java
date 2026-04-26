package com.factory.controller;

import com.factory.common.Result;
import com.factory.service.DashboardService;
import com.factory.vo.DashboardStatsVO;
import com.factory.vo.InventoryTrendVO;
import com.factory.vo.MaterialPieVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public Result<DashboardStatsVO> getStats() {
        return Result.success(dashboardService.getStats());
    }

    @GetMapping("/trend")
    public Result<List<InventoryTrendVO>> getTrend(@RequestParam(defaultValue = "7") int days) {
        return Result.success(dashboardService.getInventoryTrend(days));
    }

    @GetMapping("/pie")
    public Result<List<MaterialPieVO>> getPie() {
        return Result.success(dashboardService.getMaterialPie());
    }
}