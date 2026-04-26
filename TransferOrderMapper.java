package com.factory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.factory.entity.TransferOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 物料调拨单Mapper
 */
@Mapper
public interface TransferOrderMapper extends BaseMapper<TransferOrder> {
    // 基础CRUD已由BaseMapper提供，无需额外方法
}