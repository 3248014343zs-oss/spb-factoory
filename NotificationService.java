package com.factory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.factory.entity.Notification;
import java.util.List;

public interface NotificationService extends IService<Notification> {
    void sendLowStockAlert(Long materialId, String materialName, int currentStock);
    void sendOrderAuditResult(Long userId, Long orderId, String orderNo, Integer status);
    List<Notification> getUnreadNotifications(Long userId);
    void markAsRead(Long id, Long userId);
    void markAllAsRead(Long userId);
}