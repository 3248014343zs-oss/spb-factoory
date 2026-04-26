package com.factory.vo;
import lombok.Data;
@Data
public class DashboardStatsVO {
    private Integer totalMaterials;
    private Integer lowStockCount;
    private Integer todayInQuantity;
    private Integer todayOutQuantity;
    private Integer pendingOrders;
}