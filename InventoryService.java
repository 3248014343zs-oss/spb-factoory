package com.factory.service;

import com.factory.entity.InventoryRecord;
import java.util.List;

public interface InventoryService {
    int batchInventory(List<InventoryRecord> records);
}