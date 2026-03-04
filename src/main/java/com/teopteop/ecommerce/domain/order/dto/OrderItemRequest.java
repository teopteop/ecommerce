package com.teopteop.ecommerce.domain.order.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemRequest(
        @NotNull
        Long productId,

        @Min(value = 1, message = "1개 이상의 수량이 주문되어야 합니다.")
        @Max(value = 100, message = "1회 주문 시 최대 수량은 100개입니다.")
        int quantity
) {}
