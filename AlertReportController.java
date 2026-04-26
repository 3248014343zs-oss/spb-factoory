package com.factory.controller;

import com.alibaba.excel.EasyExcel;
import com.factory.common.Result;
import com.factory.entity.Material;
import com.factory.service.MaterialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/alert-report")
@PreAuthorize("hasAnyRole('BOSS', 'ADMIN', 'WAREHOUSE')")  // 增加 BOSS
public class AlertReportController {

    @Autowired
    private MaterialService materialService;

    @GetMapping("/list")
    public Result<List<Material>> getAlertMaterials() {
        List<Material> materials = materialService.lambdaQuery()
                .apply("stock_num < min_threshold OR stock_num > max_threshold")
                .list();
        return Result.success(materials);
    }

    @GetMapping("/export")
    public void exportAlertReport(HttpServletResponse response) throws IOException {
        List<Material> materials = materialService.lambdaQuery()
                .apply("stock_num < min_threshold OR stock_num > max_threshold")
                .list();

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("库存预警报表_" + System.currentTimeMillis(), "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(), Material.class)
                .sheet("预警物料")
                .doWrite(materials);
    }
}