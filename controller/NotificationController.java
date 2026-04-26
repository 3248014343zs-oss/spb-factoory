package com.factory.controller;

import com.factory.common.Result;
import com.factory.entity.Notification;
import com.factory.service.NotificationService;
import com.factory.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/unread")
    public Result<List<Notification>> getUnread() {
        return Result.success(notificationService.getUnreadNotifications(SecurityUtils.getCurrentUserId()));
    }

    @PutMapping("/{id}/read")
    public Result<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id, SecurityUtils.getCurrentUserId());
        return Result.success();
    }

    @PutMapping("/read-all")
    public Result<Void> markAllAsRead() {
        notificationService.markAllAsRead(SecurityUtils.getCurrentUserId());
        return Result.success();
    }
}