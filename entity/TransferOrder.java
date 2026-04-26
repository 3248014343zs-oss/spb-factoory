package com.factory.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@TableName("transfer_order")
public class TransferOrder {
    @TableId(type = IdType.AUTO)
    private Long id;

    @NotBlank(message = "调拨单号不能为空")
    private String transferNo;

    @NotNull(message = "物料ID不能为空")
    private Long materialId;

    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量必须大于0")
    private Integer quantity;

    private String fromLocation;
    private String toLocation;
    private Integer status;
    private Long applicantId;
    private Long auditorId;
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}