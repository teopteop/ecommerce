package com.teopteop.ecommerce.domain.product.dto;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record ProductCreateRequest(
        @NotBlank(message = "상품명은 필수 입력 사항입니다.")
        String name,

        @NotBlank(message = "상품가격은 필수 입력 사항입니다.")
        BigDecimal price,

        @NotBlank(message = "재고수량은 필수 입력 사항입니다.")
        int stockQuantity
) {}
