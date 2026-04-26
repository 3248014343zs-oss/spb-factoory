package com.factory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.factory.common.BusinessException;
import com.factory.entity.*;
import com.factory.mapper.*;
import com.factory.service.InventoryService;
import com.factory.service.NotificationService;
import com.factory.utils.AlertUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final MaterialStockMapper stockMapper;
    private final InventoryRecordMapper recordMapper;
    private final MaterialMapper materialMapper;
    private final NotificationService notificationService;
    private final AlertUtils alertUtils;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Retryable(value = OptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 100))
    public int batchInventory(List<InventoryRecord> records) {
        if (CollectionUtils.isEmpty(records)) {
            throw new BusinessException("出入库记录不能为空");
        }

        int successCount = 0;
        for (InventoryRecord record : records) {
            if (record.getMaterialId() == null) throw new BusinessException("物料ID不能为空");
            if (record.getWarehouseId() == null) throw new BusinessException("仓库ID不能为空");
            if (record.getLocationId() == null) throw new BusinessException("库位ID不能为空");
            if (record.getQuantity() == null || record.getQuantity() <= 0) throw new BusinessException("数量必须大于0");
            if (!"IN".equals(record.getType()) && !"OUT".equals(record.getType())) throw new BusinessException("类型错误");

            Material material = materialMapper.selectById(record.getMaterialId());
            if (material == null) throw new BusinessException("物料不存在");

            LambdaQueryWrapper<MaterialStock> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MaterialStock::getMaterialId, record.getMaterialId())
                    .eq(MaterialStock::getWarehouseId, record.getWarehouseId())
                    .eq(MaterialStock::getLocationId, record.getLocationId());
            MaterialStock stock = stockMapper.selectOne(wrapper);
            if (stock == null) {
                stock = new MaterialStock();
                stock.setMaterialId(record.getMaterialId());
                stock.setWarehouseId(record.getWarehouseId());
                stock.setLocationId(record.getLocationId());
                stock.setQuantity(0);
            }

            int currentQty = stock.getQuantity() == null ? 0 : stock.getQuantity();
            int newQty;
            if ("IN".equals(record.getType())) {
                newQty = currentQty + record.getQuantity();
            } else {
                if (currentQty < record.getQuantity()) {
                    throw new BusinessException("库存不足，当前库存：" + currentQty);
                }
                newQty = currentQty - record.getQuantity();
            }

            stock.setQuantity(newQty);
            if (stock.getId() == null) {
                stockMapper.insert(stock);
            } else {
                int rows = stockMapper.updateById(stock);
                if (rows == 0) throw new OptimisticLockingFailureException("库存并发更新冲突");
            }

            recordMapper.insert(record);

            checkAndNotify(material, newQty);
            successCount++;
        }
        return successCount;
    }

    private void checkAndNotify(Material material, int newStock) {
        boolean isLow = material.getMinThreshold() != null && newStock < material.getMinThreshold();
        boolean isHigh = material.getMaxThreshold() != null && newStock > material.getMaxThreshold();
        if (isLow || isHigh) {
            CompletableFuture.runAsync(() -> {
                try {
                    alertUtils.sendAlertNotify(material.getName(), newStock);
                    notificationService.sendLowStockAlert(material.getId(), material.getName(), newStock);
                } catch (Exception e) {
                    log.error("预警发送失败", e);
                }
            });
        }
    }
}