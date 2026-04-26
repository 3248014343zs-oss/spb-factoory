package com.factory.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.factory.common.BusinessException;
import com.factory.entity.InventoryRecord;
import com.factory.entity.ShortageReport;
import com.factory.mapper.InventoryRecordMapper;
import com.factory.mapper.ShortageReportMapper;
import com.factory.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShortageReportServiceImpl extends ServiceImpl<ShortageReportMapper, ShortageReport> {

    @Autowired
    private MaterialService materialService;

    @Autowired
    private InventoryRecordMapper recordMapper;

    @Transactional(rollbackFor = Exception.class)
    public void handleReport(Long reportId, Long handlerId, String handleRemark) {
        ShortageReport report = this.getById(reportId);
        if (report == null) {
            throw new BusinessException("上报记录不存在");
        }
        if (report.getStatus() != 0) {
            throw new BusinessException("该上报已处理");
        }

        materialService.updateStock(report.getMaterialId(), "IN", report.getRequiredQuantity());

        InventoryRecord record = new InventoryRecord();
        record.setMaterialId(report.getMaterialId());
        record.setType("IN");
        record.setQuantity(report.getRequiredQuantity());
        record.setRemark("缺料处理，上报ID：" + reportId + "，备注：" + handleRemark);
        recordMapper.insert(record);

        report.setStatus(1);
        report.setHandlerId(handlerId);
        report.setHandleRemark(handleRemark);
        this.updateById(report);
    }
}