package com.factory.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.factory.common.BusinessException;
import com.factory.entity.InventoryOrder;
import com.factory.entity.InventoryRecord;
import com.factory.mapper.InventoryOrderMapper;
import com.factory.mapper.InventoryRecordMapper;
import com.factory.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryOrderServiceImpl extends ServiceImpl<InventoryOrderMapper, InventoryOrder> {

    @Autowired
    private InventoryRecordMapper recordMapper;

    @Autowired
    private MaterialService materialService;   // 新增：用于更新库存

    @Transactional(rollbackFor = Exception.class)
    public void auditOrder(Long orderId, Integer status, Long auditorId) {
        InventoryOrder order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException("订单已审核过");
        }

        // 审核通过时，更新物料库存
        if (status == 1) {
            // 调用 MaterialService 更新库存（IN/OUT 已在订单中）
            int newStock = materialService.updateStock(order.getMaterialId(), order.getType(), order.getQuantity());
            // 记录出入库流水
            InventoryRecord record = new InventoryRecord();
            record.setOrderNo(order.getOrderNo());
            record.setMaterialId(order.getMaterialId());
            record.setType(order.getType());
            record.setQuantity(order.getQuantity());
            record.setRemark(order.getRemark());
            recordMapper.insert(record);
        }

        // 更新订单状态
        order.setStatus(status);
        order.setAuditorId(auditorId);
        this.updateById(order);
    }
}