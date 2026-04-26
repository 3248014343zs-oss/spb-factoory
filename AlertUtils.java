package com.factory.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AlertUtils {

    public void sendAlertNotify(String materialName, int currentStock) {
        log.warn("【库存预警】物料：{}，当前库存：{}", materialName, currentStock);
    }
}