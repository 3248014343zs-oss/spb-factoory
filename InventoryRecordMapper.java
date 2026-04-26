package com.factory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.factory.entity.InventoryRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 出入库记录Mapper
 */
@Mapper
public interface InventoryRecordMapper extends BaseMapper<InventoryRecord> {
    // 基础CRUD已由BaseMapper提供，无需额外方法
}