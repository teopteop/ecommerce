package com.teopteop.ecommerce.domain.product.event;

public record ProductCreatedEvent(
        int initialStockQuantity,
        Long categoryId
) {}
