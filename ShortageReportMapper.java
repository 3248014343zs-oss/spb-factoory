package com.factory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.factory.entity.ShortageReport;
import org.apache.ibatis.annotations.Mapper;

/**
 * 缺料上报Mapper
 */
@Mapper
public interface ShortageReportMapper extends BaseMapper<ShortageReport> {
    // 基础CRUD已由BaseMapper提供，无需额外方法
}