package com.teopteop.ecommerce.domain.category.dto;

import com.teopteop.ecommerce.domain.category.entity.Category;

public record CategoryResponse(
        Long id,
        String name,
        String code,
        Long parentId
) {
    public static CategoryResponse fromEntity(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getCode(),
                category.getParentId()
        );
    }
}
