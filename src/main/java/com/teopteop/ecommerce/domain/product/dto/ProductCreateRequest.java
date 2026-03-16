package com.teopteop.ecommerce.domain.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ProductCreateRequest(

        @NotBlank(message = "상품명은 필수 입력 사항입니다.")
        String name,

        @NotNull(message = "상품 가격은 필수 입력 사항입니다.")
        @PositiveOrZero(message = "상품 가격은 0 이상이어야 합니다.")
        BigDecimal price,

        @NotNull(message = "재고 수량은 필수 입력 사항입니다.")
        @PositiveOrZero(message = "재고 수량은 0 이상이어야 합니다.")
        Integer stockQuantity,

        @NotNull(message = "카테고리 ID는 필수 입력 사항입니다.")
        Long categoryId
) {}
