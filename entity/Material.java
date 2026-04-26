package com.factory.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@TableName("material")
public class Material {
    @TableId(type = IdType.AUTO)
    private Long id;
    @NotBlank(message = "物料编码不能为空")
    private String code;
    @NotBlank(message = "物料名称不能为空")
    private String name;
    private Long categoryId;
    private String spec;
    private String unit;
    private String imageUrl;
    @NotNull(message = "最低阈值不能为空")
    @Min(value = 0, message = "最低阈值不能为负数")
    private Integer minThreshold;
    @NotNull(message = "最高阈值不能为空")
    @Min(value = 1, message = "最高阈值必须大于最低阈值")
    private Integer maxThreshold;
    @Version
    private Integer version;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}