package com.teopteop.ecommerce.domain.payment.dto;

// 전액 취소용 DTO
public record TossCancelRequest(
        String cancelReason
) {}
