package com.factory.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.factory.entity.BackupRecord;
import com.factory.mapper.BackupRecordMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BackupRecordServiceImpl extends ServiceImpl<BackupRecordMapper, BackupRecord> {

    public void saveBackupRecord(String backupPath, Long operatorId) {
        BackupRecord record = new BackupRecord();
        record.setBackupTime(LocalDateTime.now());
        record.setBackupPath(backupPath);
        record.setOperatorId(operatorId);
        this.save(record);
    }
}