package com.factory.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.factory.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        // 创建时间
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        // 更新时间（插入时也填充当前时间，避免 null）
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());

        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId != null) {
            this.strictInsertFill(metaObject, "operatorId", Long.class, currentUserId);
        }
        if (metaObject.hasSetter("applicantId")) {
            this.strictInsertFill(metaObject, "applicantId", Long.class, currentUserId);
        }
        if (metaObject.hasSetter("reporterId")) {
            this.strictInsertFill(metaObject, "reporterId", Long.class, currentUserId);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }
}