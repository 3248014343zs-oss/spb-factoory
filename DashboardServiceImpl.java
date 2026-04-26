package com.factory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.factory.entity.*;
import com.factory.mapper.*;
import com.factory.service.DashboardService;
import com.factory.vo.DashboardStatsVO;
import com.factory.vo.InventoryTrendVO;
import com.factory.vo.MaterialPieVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final MaterialMapper materialMapper;
    private final InventoryRecordMapper recordMapper;
    private final InventoryOrderMapper orderMapper;
    private final MaterialStockMapper stockMapper;

    @Override
    public DashboardStatsVO getStats() {
        DashboardStatsVO vo = new DashboardStatsVO();

        // 强制转换 Long -> int
        Long totalMaterialsLong = materialMapper.selectCount(null);
        vo.setTotalMaterials(totalMaterialsLong.intValue());

        // 低库存预警：汇总所有库位的库存，按物料ID分组
        List<MaterialStock> stocks = stockMapper.selectList(null);
        Map<Long, Integer> stockMap = stocks.stream()
                .collect(Collectors.groupingBy(MaterialStock::getMaterialId,
                        Collectors.summingInt(s -> s.getQuantity() == null ? 0 : s.getQuantity())));

        List<Material> materials = materialMapper.selectList(null);
        long lowCount = materials.stream()
                .filter(m -> {
                    Integer totalStock = stockMap.getOrDefault(m.getId(), 0);
                    return totalStock < m.getMinThreshold();
                })
                .count();
        vo.setLowStockCount((int) lowCount);

        // 今日入库/出库量
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().plusDays(1).atStartOfDay();

        List<InventoryRecord> todayIn = recordMapper.selectList(new LambdaQueryWrapper<InventoryRecord>()
                .between(InventoryRecord::getCreateTime, todayStart, todayEnd)
                .eq(InventoryRecord::getType, "IN"));
        int todayInQuantity = todayIn.stream().mapToInt(InventoryRecord::getQuantity).sum();
        vo.setTodayInQuantity(todayInQuantity);

        List<InventoryRecord> todayOut = recordMapper.selectList(new LambdaQueryWrapper<InventoryRecord>()
                .between(InventoryRecord::getCreateTime, todayStart, todayEnd)
                .eq(InventoryRecord::getType, "OUT"));
        int todayOutQuantity = todayOut.stream().mapToInt(InventoryRecord::getQuantity).sum();
        vo.setTodayOutQuantity(todayOutQuantity);

        // 待审核订单数
        Long pendingOrdersLong = orderMapper.selectCount(new LambdaQueryWrapper<InventoryOrder>()
                .eq(InventoryOrder::getStatus, 0));
        vo.setPendingOrders(pendingOrdersLong.intValue());

        return vo;
    }

    @Override
    public List<InventoryTrendVO> getInventoryTrend(int days) {
        List<InventoryTrendVO> result = new ArrayList<>();
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();

            List<InventoryRecord> records = recordMapper.selectList(new LambdaQueryWrapper<InventoryRecord>()
                    .between(InventoryRecord::getCreateTime, dayStart, dayEnd));

            int inSum = records.stream()
                    .filter(r -> "IN".equals(r.getType()))
                    .mapToInt(InventoryRecord::getQuantity)
                    .sum();
            int outSum = records.stream()
                    .filter(r -> "OUT".equals(r.getType()))
                    .mapToInt(InventoryRecord::getQuantity)
                    .sum();

            InventoryTrendVO vo = new InventoryTrendVO();
            vo.setDate(date);
            vo.setInQuantity(inSum);
            vo.setOutQuantity(outSum);
            result.add(vo);
        }
        return result;
    }

    @Override
    public List<MaterialPieVO> getMaterialPie() {
        List<Material> materials = materialMapper.selectList(null);
        Map<String, Long> categoryCount = materials.stream()
                .collect(Collectors.groupingBy(
                        m -> m.getCategoryId() != null ? "分类" + m.getCategoryId() : "未分类",
                        Collectors.counting()
                ));
        return categoryCount.entrySet().stream()
                .map(entry -> new MaterialPieVO(entry.getKey(), entry.getValue().intValue()))
                .collect(Collectors.toList());
    }
}