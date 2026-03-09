package com.teopteop.ecommerce.domain.payment.dto;

import java.math.BigDecimal;

public record TossConfirmResponse(
        String paymentKey,
        String orderId,
        String method,
        BigDecimal totalAmount,
        String status,
        String approvedAt,
        String secret,                      // 가상계좌 사용 시 값 있음, 나머지는 null
        TossVirtualAccount virtualAccount   // 가상계좌 사용 시 값 있음, 나머지는 null
) {}
