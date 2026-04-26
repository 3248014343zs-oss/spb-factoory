package com.factory.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@TableName("inventory_order")
public class InventoryOrder {
    @TableId(type = IdType.AUTO)
    private Long id;

    @NotBlank(message = "订单编号不能为空")
    private String orderNo;

    @NotBlank(message = "订单类型不能为空")
    @Pattern(regexp = "IN|OUT", message = "订单类型只能是 IN（入库）或 OUT（出库）")
    private String type;

    @NotNull(message = "物料ID不能为空")
    private Long materialId;

    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量必须大于0")
    private Integer quantity;

    private Integer status;

    private Long applicantId;
    private Long auditorId;
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}