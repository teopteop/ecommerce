package com.teopteop.ecommerce.domain.product.dto;

import com.teopteop.ecommerce.domain.product.entity.Product;

import java.math.BigDecimal;

public record ProductResponse(
    Long id,
    String name,
    BigDecimal price
) {
    public static ProductResponse fromEntity(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice()
        );
    }
}
