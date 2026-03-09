package com.teopteop.ecommerce.domain.payment.dto;

import java.time.LocalDateTime;

public record TossPaymentStatusChangedRequest(
        String eventType,
        LocalDateTime createdAt,
        TossPaymentData data
) {}
