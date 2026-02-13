package com.teopteop.ecommerce.domain.inventory.dto;

import com.teopteop.ecommerce.domain.inventory.entity.Inventory;

public record InventoryResponse(
        Long id,
        Long productId,
        Integer quantity
) {
    public static InventoryResponse fromEntity(Inventory inventory) {
        return new InventoryResponse(inventory.getId(), inventory.getProductId(), inventory.getQuantity());
    }
}
