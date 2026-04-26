package com.factory.controller;

import com.factory.common.Result;
import com.factory.entity.InventoryOrder;
import com.factory.service.impl.InventoryOrderServiceImpl;
import com.factory.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/inventory-order")
public class InventoryOrderController {

    @Autowired
    private InventoryOrderServiceImpl orderService;

    @PostMapping
    @PreAuthorize("hasRole('OPERATOR')")
    public Result<Long> submitOrder(@Valid @RequestBody InventoryOrder order) {
        order.setApplicantId(SecurityUtils.getCurrentUserId());
        order.setStatus(0);
        orderService.save(order);
        log.info("生产操作员提交订单，订单号：{}", order.getOrderNo());
        return Result.success(order.getId(), "订单提交成功，等待审核");
    }

    @PutMapping("/{orderId}/audit")
    @PreAuthorize("hasRole('WAREHOUSE')")
    public Result<Void> auditOrder(@PathVariable Long orderId,
                                   @RequestParam Integer status,
                                   @RequestParam(required = false) String remark) {
        orderService.auditOrder(orderId, status, SecurityUtils.getCurrentUserId());
        log.info("仓库管理员审核订单，ID：{}，状态：{}", orderId, status);
        return Result.success();
    }

    @GetMapping("/list")
    @PreAuthorize("isAuthenticated()")
    public Result<List<InventoryOrder>> list() {
        if (SecurityUtils.hasRole("ADMIN") || SecurityUtils.hasRole("WAREHOUSE")) {
            return Result.success(orderService.list());
        } else {
            Long userId = SecurityUtils.getCurrentUserId();
            List<InventoryOrder> orders = orderService.lambdaQuery()
                    .eq(InventoryOrder::getApplicantId, userId)
                    .list();
            return Result.success(orders);
        }
    }
}