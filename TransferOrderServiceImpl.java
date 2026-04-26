package com.factory.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.factory.common.BusinessException;
import com.factory.entity.InventoryRecord;
import com.factory.entity.TransferOrder;
import com.factory.mapper.InventoryRecordMapper;
import com.factory.mapper.TransferOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransferOrderServiceImpl extends ServiceImpl<TransferOrderMapper, TransferOrder> {

    @Autowired
    private InventoryRecordMapper recordMapper;

    @Transactional(rollbackFor = Exception.class)
    public void auditTransfer(Long transferId, Integer status, Long auditorId) {
        TransferOrder transfer = this.getById(transferId);
        if (transfer == null) {
            throw new BusinessException("调拨单不存在");
        }
        if (transfer.getStatus() != 0) {
            throw new BusinessException("调拨单已审核过");
        }

        transfer.setStatus(status);
        transfer.setAuditorId(auditorId);
        this.updateById(transfer);

        if (status == 1) {
            // 调拨：库存总量不变，只记录两条流水（出库+入库），但不更新物料表（避免重复加减）
            // 注意：原代码中 updateStock 导致库存净变化为 0 且浪费性能，现已移除
            InventoryRecord outRecord = new InventoryRecord();
            outRecord.setMaterialId(transfer.getMaterialId());
            outRecord.setType("OUT");
            outRecord.setQuantity(transfer.getQuantity());
            outRecord.setRemark("调拨出库，调拨单号：" + transfer.getTransferNo() + "，调出库位：" + transfer.getFromLocation());
            recordMapper.insert(outRecord);

            InventoryRecord inRecord = new InventoryRecord();
            inRecord.setMaterialId(transfer.getMaterialId());
            inRecord.setType("IN");
            inRecord.setQuantity(transfer.getQuantity());
            inRecord.setRemark("调拨入库，调拨单号：" + transfer.getTransferNo() + "，目标库位：" + transfer.getToLocation());
            recordMapper.insert(inRecord);

            // 若物料表有库位字段，此处应更新库位信息（本系统暂无，可扩展）
        }
    }
}