package com.teopteop.ecommerce.domain.payment.dto;

import com.teopteop.ecommerce.domain.payment.entity.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public sealed interface PaymentConfirmResponse
        permits PaymentConfirmResponse.Instant, PaymentConfirmResponse.VirtualAccount {

    // 즉시 승인 결제 (카드, 계좌이체, 휴대폰, 간편결제 등)
    record Instant(
            String orderNumber,
            BigDecimal totalAmount,
            PaymentMethod method,
            LocalDateTime approvedAt
    ) implements PaymentConfirmResponse {}

    // 가상계좌: 입금 대기
    record VirtualAccount(
            String orderNumber,
            BigDecimal totalAmount,
            String accountNumber,
            String bankCode,
            LocalDateTime dueDate
    ) implements PaymentConfirmResponse {}
}

