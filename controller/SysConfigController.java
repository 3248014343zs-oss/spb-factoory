package com.factory.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.factory.common.Result;
import com.factory.entity.SysConfig;
import com.factory.service.SysConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
public class SysConfigController {

    private final SysConfigService configService;  // 改用 Service

    @GetMapping("/theme")
    public Result<String> getTheme() {
        SysConfig config = configService.getOne(new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getConfigKey, "system_theme"));
        return Result.success(config != null ? config.getConfigValue() : "{}");
    }

    @PutMapping("/theme")
    @PreAuthorize("hasAnyRole('BOSS','ADMIN')")
    public Result<Void> updateTheme(@RequestBody String themeJson) {
        SysConfig config = configService.getOne(new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getConfigKey, "system_theme"));
        if (config == null) {
            config = new SysConfig();
            config.setConfigKey("system_theme");
            config.setDescription("系统主题配置");
        }
        config.setConfigValue(themeJson);
        configService.saveOrUpdate(config);  // Service 层有此方法
        return Result.success();
    }
}