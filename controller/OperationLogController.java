package com.factory.controller;

import com.factory.common.Result;
import com.factory.entity.OperationLog;
import com.factory.service.impl.OperationLogServiceImpl;
import com.factory.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/logs")
public class OperationLogController {

    @Autowired
    private OperationLogServiceImpl logService;

    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('BOSS', 'ADMIN')")  // 增加 BOSS
    public Result<List<OperationLog>> list() {
        log.info("查询所有操作日志");
        return Result.success(logService.list());
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public Result<List<OperationLog>> listMyLogs() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<OperationLog> list = logService.lambdaQuery()
                .eq(OperationLog::getUserId, userId)
                .orderByDesc(OperationLog::getCreateTime)
                .list();
        return Result.success(list);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('BOSS', 'ADMIN')")  // 增加 BOSS
    public Result<List<OperationLog>> listByUserId(@PathVariable Long userId) {
        log.info("查询用户操作日志，用户ID：{}", userId);
        List<OperationLog> list = logService.lambdaQuery()
                .eq(OperationLog::getUserId, userId)
                .list();
        return Result.success(list);
    }
}