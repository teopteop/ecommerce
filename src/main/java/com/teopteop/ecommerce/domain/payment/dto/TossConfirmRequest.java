package com.teopteop.ecommerce.domain.payment.dto;

import java.math.BigDecimal;

public record TossConfirmRequest(
        String paymentKey,
        String orderId,     // 토스 스펙 픽드명, 우리 프로젝트에선 orderNumber(UUID)
        BigDecimal amount
) {}
