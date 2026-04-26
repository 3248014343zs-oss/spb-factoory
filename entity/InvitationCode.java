package com.factory.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("invitation_code")
public class InvitationCode {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String code;
    private String targetRole;
    private Long creatorId;
    private String creatorRole;
    private Boolean used;
    private Long usedBy;
    private LocalDateTime expireTime;
    @Version
    private Integer version;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}