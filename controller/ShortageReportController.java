package com.factory.controller;

import com.factory.common.Result;
import com.factory.entity.ShortageReport;
import com.factory.service.impl.ShortageReportServiceImpl;
import com.factory.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/shortage")
public class ShortageReportController {

    @Autowired
    private ShortageReportServiceImpl reportService;

    @PostMapping
    @PreAuthorize("hasRole('OPERATOR')")
    public Result<Long> report(@Valid @RequestBody ShortageReport report) {
        report.setReporterId(SecurityUtils.getCurrentUserId());
        report.setStatus(0);
        reportService.save(report);
        log.info("生产操作员上报缺料，物料ID：{}，需求数量：{}", report.getMaterialId(), report.getRequiredQuantity());
        return Result.success(report.getId(), "缺料上报成功");
    }

    @PutMapping("/{reportId}/handle")
    @PreAuthorize("hasRole('WAREHOUSE')")
    public Result<Void> handleReport(@PathVariable Long reportId,
                                     @RequestParam String remark) {
        reportService.handleReport(reportId, SecurityUtils.getCurrentUserId(), remark);
        log.info("仓库管理员处理缺料上报，ID：{}，备注：{}", reportId, remark);
        return Result.success();
    }

    @GetMapping("/list")
    @PreAuthorize("isAuthenticated()")
    public Result<List<ShortageReport>> list() {
        if (SecurityUtils.hasRole("ADMIN") || SecurityUtils.hasRole("WAREHOUSE")) {
            return Result.success(reportService.list());
        } else {
            Long userId = SecurityUtils.getCurrentUserId();
            List<ShortageReport> reports = reportService.lambdaQuery()
                    .eq(ShortageReport::getReporterId, userId)
                    .list();
            return Result.success(reports);
        }
    }
}