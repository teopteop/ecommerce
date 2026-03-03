package com.teopteop.ecommerce.domain.payment.dto;

import com.teopteop.ecommerce.domain.payment.entity.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentConfirmResponse(
        String orderNumber,
        BigDecimal totalAmount,
        PaymentMethod method,
        LocalDateTime approvedAt
) {}
