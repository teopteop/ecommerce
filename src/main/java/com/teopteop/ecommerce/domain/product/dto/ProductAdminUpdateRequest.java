package com.teopteop.ecommerce.domain.product.dto;

import com.teopteop.ecommerce.domain.product.entity.ProductStatus;

import java.math.BigDecimal;

public record ProductAdminUpdateRequest(
        String name,
        BigDecimal price,
        ProductStatus status
) {}
