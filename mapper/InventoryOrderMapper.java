package com.factory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.factory.entity.InventoryOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 出入库订单Mapper
 */
@Mapper
public interface InventoryOrderMapper extends BaseMapper<InventoryOrder> {
    // 基础CRUD已由BaseMapper提供，无需额外方法
}