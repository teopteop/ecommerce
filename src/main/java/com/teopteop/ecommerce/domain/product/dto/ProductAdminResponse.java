package com.teopteop.ecommerce.domain.product.dto;

import com.teopteop.ecommerce.domain.product.entity.Product;
import com.teopteop.ecommerce.domain.product.entity.ProductStatus;

import java.math.BigDecimal;

public record ProductAdminResponse(
        Long id,
        String name,
        BigDecimal price,
        Integer stockQuantity,
        ProductStatus status,
        String categoryName,
        Long categoryId,
        boolean deleted
) {
    public static ProductAdminResponse of(
            Product product,
            Integer stockQuantity,
            String categoryName,
            Long categoryId
    ) {
        return new ProductAdminResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                stockQuantity,
                product.getStatus(),
                categoryName,
                categoryId,
                product.isDeleted()
        );
    }
}
