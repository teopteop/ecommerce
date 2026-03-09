package com.teopteop.ecommerce.domain.payment.dto;

import java.math.BigDecimal;

// 부분 취소용 DTO
public record TossPartialCancelRequest(
        String cancelReason,
        BigDecimal cancelAmount
) {}
