package com.factory.vo;

import lombok.Data;

@Data
public class StockStatsVO {
    private Long materialId;
    private String materialName;
    private Integer totalIn;
    private Integer totalOut;
    private Integer netChange;
}