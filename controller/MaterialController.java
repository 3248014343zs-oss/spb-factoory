package com.factory.controller;

import com.factory.common.Result;
import com.factory.entity.Material;
import com.factory.service.MaterialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/material")
@RequiredArgsConstructor
public class MaterialController {

    private final MaterialService materialService;

    @GetMapping("/list")
    @PreAuthorize("isAuthenticated()")
    public Result<List<Material>> list() {
        return Result.success(materialService.list());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','WAREHOUSE')")
    public Result<Long> add(@Valid @RequestBody Material material) {
        return Result.success(materialService.add(material));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','WAREHOUSE')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody Material material) {
        material.setId(id);
        materialService.updateById(material);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        materialService.removeById(id);
        return Result.success();
    }
}