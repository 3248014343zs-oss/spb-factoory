package com.factory.controller;

import com.alibaba.excel.EasyExcel;
import com.factory.common.Result;
import com.factory.service.ReportService;
import com.factory.vo.InventoryTrendVO;
import com.factory.vo.StockStatsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/report")
@PreAuthorize("hasAnyRole('BOSS', 'ADMIN', 'WAREHOUSE')")  // 增加 BOSS
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/inventory-trend")
    public Result<List<InventoryTrendVO>> inventoryTrend(@RequestParam(required = false) Long materialId,
                                                         @RequestParam(defaultValue = "30") int days) {
        List<InventoryTrendVO> trend = reportService.getInventoryTrend(materialId, days);
        return Result.success(trend);
    }

    @GetMapping("/stock-stats")
    public Result<StockStatsVO> stockStats(@RequestParam String startTime,
                                           @RequestParam String endTime,
                                           @RequestParam(required = false) Long materialId) {
        StockStatsVO stats = reportService.getStockStats(startTime, endTime, materialId);
        return Result.success(stats);
    }

    @GetMapping("/export-stock-stats")
    public void exportStockStats(@RequestParam String startTime,
                                 @RequestParam String endTime,
                                 @RequestParam(required = false) Long materialId,
                                 HttpServletResponse response) throws IOException {
        List<StockStatsVO> data = reportService.getStockStatsList(startTime, endTime, materialId);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("出入库统计_" + startTime + "_" + endTime, "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(), StockStatsVO.class)
                .sheet("统计报表")
                .doWrite(data);
    }
}