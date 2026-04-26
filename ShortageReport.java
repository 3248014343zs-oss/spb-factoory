package com.factory.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@TableName("shortage_report")
public class ShortageReport {
    @TableId(type = IdType.AUTO)
    private Long id;

    @NotNull(message = "物料ID不能为空")
    private Long materialId;

    @NotNull(message = "需求数量不能为空")
    @Min(value = 1, message = "需求数量必须大于0")
    private Integer requiredQuantity;

    private String reason;

    private Long reporterId;
    private Integer status;
    private Long handlerId;
    private String handleRemark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}