package com.factory.controller;

import com.factory.common.Result;
import com.factory.service.impl.BackupRecordServiceImpl;
import com.factory.utils.BackupUtils;
import com.factory.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/backup")
@PreAuthorize("hasAnyRole('BOSS', 'ADMIN')")  // 增加 BOSS
public class BackupController {

    @Autowired
    private BackupUtils backupUtils;

    @Autowired
    private BackupRecordServiceImpl backupRecordService;

    @PostMapping("/database")
    public Result<String> backupDatabase() {
        log.info("执行数据库备份");
        String backupPath = backupUtils.backup();
        backupRecordService.saveBackupRecord(backupPath, SecurityUtils.getCurrentUserId());
        return Result.success(backupPath, "备份成功");
    }
}