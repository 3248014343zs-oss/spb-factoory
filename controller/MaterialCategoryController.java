package com.factory.controller;

import com.factory.common.Result;
import com.factory.entity.MaterialCategory;
import com.factory.service.impl.MaterialCategoryServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/material-category")
@PreAuthorize("hasAnyRole('BOSS', 'ADMIN')")   // 关键修改：允许 BOSS 和 ADMIN
public class MaterialCategoryController {

    @Autowired
    private MaterialCategoryServiceImpl categoryService;

    @GetMapping("/list")
    public Result<List<MaterialCategory>> list() {
        log.info("查询物料分类列表");
        return Result.success(categoryService.list());
    }

    @PostMapping
    public Result<Long> add(@Valid @RequestBody MaterialCategory category) {
        log.info("添加物料分类：{}", category.getName());
        categoryService.save(category);
        return Result.success(category.getId(), "添加成功");
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody MaterialCategory category) {
        category.setId(id);
        categoryService.updateById(category);
        log.info("更新物料分类，ID：{}", id);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.removeById(id);
        log.info("删除物料分类，ID：{}", id);
        return Result.success();
    }
}