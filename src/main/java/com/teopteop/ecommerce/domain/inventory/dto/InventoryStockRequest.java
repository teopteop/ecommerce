package com.teopteop.ecommerce.domain.inventory.dto;

import com.teopteop.ecommerce.domain.inventory.entity.InventoryAdjustReason;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record InventoryStockRequest(
        @Positive int amount,
        @NotNull InventoryAdjustReason reason
){}
