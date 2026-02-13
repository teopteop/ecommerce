package com.teopteop.ecommerce.domain.category.dto;

public record CategoryUpdateRequest(
        String name,
        String code,
        Long parentId
) {}
