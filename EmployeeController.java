package com.factory.controller;

import com.factory.common.Result;
import com.factory.entity.User;
import com.factory.service.impl.UserServiceImpl;
import com.factory.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    @Autowired
    private UserServiceImpl userService;

    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('BOSS', 'ADMIN', 'WAREHOUSE')")
    public Result<List<User>> getEmployeeList() {
        Long currentUid = SecurityUtils.getCurrentUid();
        String currentRole = SecurityUtils.getCurrentRole();
        List<User> employees = userService.getEmployeesByManager(currentUid, currentRole);
        employees.forEach(e -> e.setPassword(null));
        return Result.success(employees);
    }

    @PutMapping("/downgrade/{uid}")
    @PreAuthorize("hasAnyRole('BOSS', 'ADMIN', 'WAREHOUSE')")
    public Result<Void> downgradeEmployee(@PathVariable Long uid) {
        Long currentUid = SecurityUtils.getCurrentUid();
        String currentRole = SecurityUtils.getCurrentRole();
        userService.downgradeEmployee(currentUid, currentRole, uid);
        return Result.success(null, "降级成功");
    }

    @DeleteMapping("/delete/{uid}")
    @PreAuthorize("hasAnyRole('BOSS', 'ADMIN', 'WAREHOUSE')")
    public Result<Void> deleteEmployee(@PathVariable Long uid) {
        Long currentUid = SecurityUtils.getCurrentUid();
        String currentRole = SecurityUtils.getCurrentRole();
        userService.deleteEmployee(currentUid, currentRole, uid);
        return Result.success(null, "删除成功");
    }
}