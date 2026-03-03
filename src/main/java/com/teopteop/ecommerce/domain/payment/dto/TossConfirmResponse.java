package com.teopteop.ecommerce.domain.payment.dto;

import java.math.BigDecimal;

public record TossConfirmResponse(
        String paymentKey,
        String orderId,
        String method,
        BigDecimal totalAmount,
        String status,
        String approvedAt
) {}
