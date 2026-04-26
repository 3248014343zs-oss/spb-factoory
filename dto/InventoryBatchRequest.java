package com.factory.dto;

import com.factory.entity.InventoryRecord;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class InventoryBatchRequest {
    @NotEmpty(message = "出入库记录不能为空")
    private List<@Valid InventoryRecord> records;
}