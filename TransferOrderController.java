package com.factory.controller;

import com.factory.common.Result;
import com.factory.entity.TransferOrder;
import com.factory.service.impl.TransferOrderServiceImpl;
import com.factory.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/transfer-order")
public class TransferOrderController {

    @Autowired
    private TransferOrderServiceImpl transferService;

    @PostMapping
    @PreAuthorize("hasRole('WAREHOUSE')")
    public Result<Long> submitTransfer(@Valid @RequestBody TransferOrder transfer) {
        transfer.setApplicantId(SecurityUtils.getCurrentUserId());
        transfer.setStatus(0);
        transferService.save(transfer);
        log.info("提交调拨申请，调拨单号：{}", transfer.getTransferNo());
        return Result.success(transfer.getId(), "调拨申请提交成功");
    }

    @PutMapping("/{transferId}/audit")
    @PreAuthorize("hasRole('WAREHOUSE')")
    public Result<Void> auditTransfer(@PathVariable Long transferId,
                                      @RequestParam Integer status,
                                      @RequestParam(required = false) String remark) {
        transferService.auditTransfer(transferId, status, SecurityUtils.getCurrentUserId());
        log.info("审核调拨单，ID：{}，状态：{}", transferId, status);
        return Result.success();
    }

    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE')")
    public Result<List<TransferOrder>> list() {
        return Result.success(transferService.list());
    }
}