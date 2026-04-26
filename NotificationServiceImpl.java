package com.factory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.factory.entity.Notification;
import com.factory.entity.User;
import com.factory.mapper.NotificationMapper;
import com.factory.mapper.UserMapper;
import com.factory.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

    private final UserMapper userMapper;

    @Override
    public void sendLowStockAlert(Long materialId, String materialName, int currentStock) {
        List<User> admins = userMapper.selectList(new LambdaQueryWrapper<User>()
                .in(User::getRole, "BOSS", "ADMIN", "WAREHOUSE"));
        for (User admin : admins) {
            Notification notification = new Notification();
            notification.setUserId(admin.getId());
            notification.setType("LOW_STOCK");
            notification.setTitle("库存预警");
            notification.setContent(String.format("物料【%s】库存不足，当前库存：%d，请及时补货", materialName, currentStock));
            notification.setRelatedId(materialId);
            this.save(notification);
        }
    }

    @Override
    public void sendOrderAuditResult(Long userId, Long orderId, String orderNo, Integer status) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType("ORDER_AUDIT");
        notification.setTitle("订单审核结果");
        String statusText = status == 1 ? "通过" : "驳回";
        notification.setContent(String.format("您的订单【%s】审核%s", orderNo, statusText));
        notification.setRelatedId(orderId);
        this.save(notification);
    }

    @Override
    public List<Notification> getUnreadNotifications(Long userId) {
        return this.list(new LambdaQueryWrapper<Notification>()
                .eq(Notification::getUserId, userId)
                .eq(Notification::getIsRead, 0)
                .orderByDesc(Notification::getCreateTime));
    }

    @Override
    public void markAsRead(Long id, Long userId) {
        Notification notification = this.getById(id);
        if (notification != null && notification.getUserId().equals(userId)) {
            notification.setIsRead(1);
            this.updateById(notification);
        }
    }

    @Override
    public void markAllAsRead(Long userId) {
        List<Notification> unread = this.list(new LambdaQueryWrapper<Notification>()
                .eq(Notification::getUserId, userId)
                .eq(Notification::getIsRead, 0));
        for (Notification n : unread) {
            n.setIsRead(1);
        }
        this.updateBatchById(unread);
    }
}