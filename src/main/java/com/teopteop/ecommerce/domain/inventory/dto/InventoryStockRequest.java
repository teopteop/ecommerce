package com.teopteop.ecommerce.domain.inventory.dto;

import com.teopteop.ecommerce.domain.inventory.entity.InventoryAdjustReason;

public record InventoryStockRequest(
        int amount,
        InventoryAdjustReason reason
){}
