package com.teopteop.ecommerce.domain.order.dto;

import com.teopteop.ecommerce.domain.order.entity.OrderCancelReason;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record OrderPartialCancelRequest(
        @NotNull(message = "취소 사유는 필수 입력 사항입니다.")
        @Size(min = 1, message = "취소 품목은 1개 이상이어야 합니다.")
        List<Long> orderItemIds,

        @NotNull(message = "취소 사유는 필수 입력 사항입니다.")
        OrderCancelReason cancelReason
) {}
