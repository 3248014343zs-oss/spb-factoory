package com.factory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.factory.entity.BackupRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统备份记录Mapper
 */
@Mapper
public interface BackupRecordMapper extends BaseMapper<BackupRecord> {
    // 基础CRUD已由BaseMapper提供，无需额外方法
}