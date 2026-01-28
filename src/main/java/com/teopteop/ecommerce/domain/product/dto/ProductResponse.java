package com.teopteop.ecommerce.domain.product.dto;

import com.teopteop.ecommerce.domain.product.entity.Product;
import com.teopteop.ecommerce.domain.product.entity.ProductStatus;

import java.math.BigDecimal;

public record ProductResponse(
    Long productId,
    String name,
    BigDecimal price,
    Integer stockQuantity,
    ProductStatus status
) {
    public static ProductResponse fromEntity(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getStatus()
        );
    }
}
