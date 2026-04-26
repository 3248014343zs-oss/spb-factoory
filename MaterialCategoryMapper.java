package com.factory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.factory.entity.MaterialCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 物料分类Mapper
 */
@Mapper
public interface MaterialCategoryMapper extends BaseMapper<MaterialCategory> {
    // 基础CRUD已由BaseMapper提供，无需额外方法
}