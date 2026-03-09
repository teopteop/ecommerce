package com.teopteop.ecommerce.domain.order.dto;

import com.teopteop.ecommerce.domain.order.entity.OrderCancelReason;
import jakarta.validation.constraints.NotNull;

public record OrderCancelRequest(
        @NotNull(message = "취소 사유는 필수 입력 사항입니다.")
        OrderCancelReason cancelReason
) {}
