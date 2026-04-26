package com.factory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.factory.entity.InventoryRecord;
import com.factory.entity.Material;
import com.factory.mapper.InventoryRecordMapper;
import com.factory.mapper.MaterialMapper;
import com.factory.service.ReportService;
import com.factory.vo.InventoryTrendVO;
import com.factory.vo.StockStatsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private MaterialMapper materialMapper;

    @Autowired
    private InventoryRecordMapper recordMapper;

    @Override
    public List<InventoryTrendVO> getInventoryTrend(Long materialId, int days) {
        // 趋势数据需要库存快照表，此处返回空列表
        return new ArrayList<>();
    }

    @Override
    public StockStatsVO getStockStats(String startTime, String endTime, Long materialId) {
        LambdaQueryWrapper<InventoryRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.between(InventoryRecord::getCreateTime, startTime, endTime);
        if (materialId != null) {
            wrapper.eq(InventoryRecord::getMaterialId, materialId);
        }
        List<InventoryRecord> records = recordMapper.selectList(wrapper);
        int totalIn = records.stream().filter(r -> "IN".equals(r.getType())).mapToInt(InventoryRecord::getQuantity).sum();
        int totalOut = records.stream().filter(r -> "OUT".equals(r.getType())).mapToInt(InventoryRecord::getQuantity).sum();
        StockStatsVO stats = new StockStatsVO();
        stats.setTotalIn(totalIn);
        stats.setTotalOut(totalOut);
        stats.setNetChange(totalIn - totalOut);
        return stats;
    }

    @Override
    public List<StockStatsVO> getStockStatsList(String startTime, String endTime, Long materialId) {
        LambdaQueryWrapper<InventoryRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.between(InventoryRecord::getCreateTime, startTime, endTime);
        if (materialId != null) {
            wrapper.eq(InventoryRecord::getMaterialId, materialId);
        }
        List<InventoryRecord> records = recordMapper.selectList(wrapper);
        Map<Long, List<InventoryRecord>> groupByMaterial = records.stream().collect(Collectors.groupingBy(InventoryRecord::getMaterialId));
        List<StockStatsVO> result = new ArrayList<>();
        for (Map.Entry<Long, List<InventoryRecord>> entry : groupByMaterial.entrySet()) {
            Long mid = entry.getKey();
            Material material = materialMapper.selectById(mid);
            int totalIn = entry.getValue().stream().filter(r -> "IN".equals(r.getType())).mapToInt(InventoryRecord::getQuantity).sum();
            int totalOut = entry.getValue().stream().filter(r -> "OUT".equals(r.getType())).mapToInt(InventoryRecord::getQuantity).sum();
            StockStatsVO vo = new StockStatsVO();
            vo.setMaterialId(mid);
            vo.setMaterialName(material != null ? material.getName() : "未知");
            vo.setTotalIn(totalIn);
            vo.setTotalOut(totalOut);
            vo.setNetChange(totalIn - totalOut);
            result.add(vo);
        }
        return result;
    }
}