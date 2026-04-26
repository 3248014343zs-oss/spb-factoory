package com.factory.controller;

import com.factory.common.Result;
import com.factory.service.impl.InvitationCodeServiceImpl;
import com.factory.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/invite")
public class InvitationCodeController {

    @Autowired
    private InvitationCodeServiceImpl invitationCodeService;

    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('BOSS', 'ADMIN', 'WAREHOUSE')")
    public Result<Map<String, String>> generateCode(@RequestParam String targetRole) {
        Long userId = SecurityUtils.getCurrentUserId();
        String code = invitationCodeService.generateCode(userId, targetRole);
        Map<String, String> data = new HashMap<>();
        data.put("code", code);
        return Result.success(data, "邀请码生成成功，有效期7天");
    }

    @PostMapping("/use")
    @PreAuthorize("isAuthenticated()")
    public Result<Void> useCode(@RequestParam String code) {
        Long userId = SecurityUtils.getCurrentUserId();
        invitationCodeService.useCode(code, userId);
        return Result.success(null, "角色升级成功，请重新登录");
    }

    @GetMapping("/my-codes")
    @PreAuthorize("hasAnyRole('BOSS', 'ADMIN', 'WAREHOUSE')")
    public Result<?> getMyCodes() {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(invitationCodeService.getMyGeneratedCodes(userId));
    }
}