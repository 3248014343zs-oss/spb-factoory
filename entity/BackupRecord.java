package com.factory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("backup_record")
public class BackupRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    private LocalDateTime backupTime;
    private String backupPath;
    private Long size;
    private Long operatorId;
    private String remark;
}