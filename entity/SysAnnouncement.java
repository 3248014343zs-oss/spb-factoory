package com.factory.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@TableName("sys_announcement")
public class SysAnnouncement {
    @TableId(type = IdType.AUTO)
    private Long id;

    @NotBlank(message = "标题不能为空")
    private String title;

    private String content;
    private Integer type;
    private Integer status;
    private Long createBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}