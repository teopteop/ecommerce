package com.teopteop.ecommerce.domain.payment.dto;

import java.time.LocalDateTime;

public record TossVirtualAccount(
        String accountNumber,
        String bankCode,
        String customerName,
        LocalDateTime dueDate
) {}
