package com.factory.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("storage_location")
public class StorageLocation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long warehouseId;
    private String code;
    private String name;
    private String rowNo;
    private String columnNo;
    private String levelNo;
    private Integer capacity;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}