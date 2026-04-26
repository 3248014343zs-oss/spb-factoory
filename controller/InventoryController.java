package com.factory.controller;

import com.factory.common.Result;
import com.factory.dto.InventoryBatchRequest;
import com.factory.entity.InventoryRecord;
import com.factory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;

@Slf4j
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/batch")
    @PreAuthorize("hasAnyRole('BOSS','ADMIN','WAREHOUSE')")
    public Result<Integer> batchInventory(@Valid @RequestBody InventoryBatchRequest request) {
        return Result.success(inventoryService.batchInventory(request.getRecords()));
    }

    @PostMapping("/single")
    @PreAuthorize("hasAnyRole('BOSS','ADMIN','WAREHOUSE')")
    public Result<Void> singleInventory(@Valid @RequestBody InventoryRecord record) {
        inventoryService.batchInventory(Collections.singletonList(record));
        return Result.success();
    }
}