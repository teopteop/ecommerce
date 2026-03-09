package com.teopteop.ecommerce.domain.payment.dto;

import java.time.LocalDateTime;

public record TossDepositCallbackRequest(
        LocalDateTime createdAt,
        String secret,
        String status,
        String transactionKey,
        String orderId // orderNumber(UUID)
) {
}
