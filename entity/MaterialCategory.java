package com.factory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@TableName("material_category")
public class MaterialCategory {
    @TableId(type = IdType.AUTO)
    private Long id;

    @NotBlank(message = "分类名称不能为空")
    private String name;

    private Long parentId;
    private Integer sort;
    private LocalDateTime createTime;
}