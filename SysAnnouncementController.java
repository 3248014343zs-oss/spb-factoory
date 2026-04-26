package com.factory.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.factory.common.Result;
import com.factory.entity.SysAnnouncement;
import com.factory.service.SysAnnouncementService;
import com.factory.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/announcement")
public class SysAnnouncementController {

    @Autowired
    private SysAnnouncementService announcementService;

    @GetMapping("/published")
    @PreAuthorize("isAuthenticated()")
    public Result<List<SysAnnouncement>> getPublished() {
        List<SysAnnouncement> list = announcementService.lambdaQuery()
                .eq(SysAnnouncement::getStatus, 1)
                .orderByDesc(SysAnnouncement::getCreateTime)
                .list();
        return Result.success(list);
    }

    @GetMapping("/page")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<SysAnnouncement>> page(@RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "10") int size,
                                              @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<SysAnnouncement> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(SysAnnouncement::getTitle, keyword).or().like(SysAnnouncement::getContent, keyword);
        }
        wrapper.orderByDesc(SysAnnouncement::getCreateTime);
        Page<SysAnnouncement> pageResult = announcementService.page(new Page<>(page, size), wrapper);
        return Result.success(pageResult);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Long> add(@Valid @RequestBody SysAnnouncement announcement) {
        announcement.setCreateBy(SecurityUtils.getCurrentUserId());
        announcementService.save(announcement);
        log.info("新增公告：{}", announcement.getTitle());
        return Result.success(announcement.getId(), "公告发布成功");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody SysAnnouncement announcement) {
        announcement.setId(id);
        announcementService.updateById(announcement);
        log.info("更新公告：{}", id);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        announcementService.removeById(id);
        log.info("删除公告：{}", id);
        return Result.success();
    }
}