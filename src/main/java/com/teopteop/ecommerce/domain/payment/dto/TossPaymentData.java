package com.teopteop.ecommerce.domain.payment.dto;

public record TossPaymentData(
        String paymentKey,
        String orderId,      // 토스 스펙 필드명, 프로젝트에선 orderNumber(UUID)
        String status,       // PAYMENT_STATUS_CHANGED 일반결제 전용, 없을 시 null
        String method       // DONE 웹훅 수신 시 결제 수단 식별용
) {}
