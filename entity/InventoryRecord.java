package com.factory.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@TableName("inventory_record")
public class InventoryRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    @NotNull(message = "物料ID不能为空")
    private Long materialId;
    @NotNull(message = "仓库ID不能为空")
    private Long warehouseId;
    @NotNull(message = "库位ID不能为空")
    private Long locationId;
    @NotBlank(message = "操作类型不能为空")
    @Pattern(regexp = "IN|OUT", message = "操作类型只能是IN或OUT")
    private String type;
    @NotNull(message = "操作数量不能为空")
    @Min(value = 1, message = "操作数量必须大于0")
    private Integer quantity;
    @TableField(fill = FieldFill.INSERT)
    private Long operatorId;
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}