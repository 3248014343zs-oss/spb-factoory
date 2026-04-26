package com.factory.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("material_stock")
public class MaterialStock {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long materialId;
    private Long warehouseId;
    private Long locationId;
    private Integer quantity;
    @Version
    private Integer version;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}