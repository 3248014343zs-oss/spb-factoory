package com.factory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.factory.common.BusinessException;
import com.factory.entity.Material;
import com.factory.mapper.MaterialMapper;
import com.factory.service.MaterialService;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MaterialServiceImpl extends ServiceImpl<MaterialMapper, Material> implements MaterialService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(Material material) {
        LambdaQueryWrapper<Material> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Material::getCode, material.getCode());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("物料编码已存在");
        }
        if (material.getMaxThreshold() != null && material.getMinThreshold() != null) {
            if (material.getMaxThreshold() <= material.getMinThreshold()) {
                throw new BusinessException("最高阈值必须大于最低阈值");
            }
        }
        this.save(material);
        return material.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Retryable(value = OptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 100))
    public int updateStock(Long id, String type, int quantity) {
        Material material = this.getById(id);
        if (material == null) {
            throw new BusinessException("物料不存在");
        }
        // 注意：此方法仅适用于单仓库场景，多仓库请使用 InventoryServiceImpl
        throw new BusinessException("请使用出入库接口进行库存操作");
    }
}