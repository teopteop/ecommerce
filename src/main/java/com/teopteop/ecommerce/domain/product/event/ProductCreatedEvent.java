package com.teopteop.ecommerce.domain.product.event;

public record ProductCreatedEvent(
        Long productId,
        int initialStockQuantity,
        Long categoryId
) {}
