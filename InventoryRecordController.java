package com.factory.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.factory.common.Result;
import com.factory.entity.InventoryRecord;
import com.factory.service.impl.InventoryRecordServiceImpl;
import com.factory.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Slf4j
@RestController
@RequestMapping("/api/inventory-record")
@RequiredArgsConstructor
public class InventoryRecordController {

    private final InventoryRecordServiceImpl recordService;

    @GetMapping("/page")
    @PreAuthorize("isAuthenticated()")
    public Result<Page<InventoryRecord>> page(@RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
                                              @RequestParam(required = false) Long materialId,
                                              @RequestParam(required = false) String type,
                                              @RequestParam(required = false) String startTime,
                                              @RequestParam(required = false) String endTime) {
        LambdaQueryWrapper<InventoryRecord> wrapper = new LambdaQueryWrapper<>();
        if (!SecurityUtils.hasRole("ADMIN") && !SecurityUtils.hasRole("WAREHOUSE")) {
            wrapper.eq(InventoryRecord::getOperatorId, SecurityUtils.getCurrentUserId());
        }
        if (materialId != null) wrapper.eq(InventoryRecord::getMaterialId, materialId);
        if (type != null) wrapper.eq(InventoryRecord::getType, type);
        if (startTime != null) wrapper.ge(InventoryRecord::getCreateTime, startTime);
        if (endTime != null) wrapper.le(InventoryRecord::getCreateTime, endTime);
        wrapper.orderByDesc(InventoryRecord::getCreateTime);
        return Result.success(recordService.page(new Page<>(page, size), wrapper));
    }
}