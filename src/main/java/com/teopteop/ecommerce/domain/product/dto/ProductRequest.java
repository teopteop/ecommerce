package com.teopteop.ecommerce.domain.product.dto;

import com.teopteop.ecommerce.domain.product.domain.ProductStatus;

import java.math.BigDecimal;

public record ProductRequest(
        String name,
        BigDecimal price,
        Integer stockQuantity, // null 허용
        ProductStatus status
) {}
