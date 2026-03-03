package com.teopteop.ecommerce.domain.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentConfirmRequest(
        @NotBlank(message = "주문번호는 필수 입력 사항입니다.")
        String orderNumber,

        @NotBlank(message = "결제 키는 필수 입력 사항입니다.")
        String paymentKey,

        @NotNull(message = "금액은 필수 입력 사항입니다.")
        BigDecimal amount
) {}
