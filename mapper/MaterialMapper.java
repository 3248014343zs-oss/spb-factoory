package com.factory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.factory.entity.Material;
import org.apache.ibatis.annotations.Mapper;

/**
 * 物料Mapper
 */
@Mapper
public interface MaterialMapper extends BaseMapper<Material> {
    // 基础CRUD已由BaseMapper提供，无需额外方法
}