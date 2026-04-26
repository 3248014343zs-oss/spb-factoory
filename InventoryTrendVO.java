package com.factory.vo;
import lombok.Data;
import java.time.LocalDate;
@Data
public class InventoryTrendVO {
    private LocalDate date;
    private Integer inQuantity;
    private Integer outQuantity;
}