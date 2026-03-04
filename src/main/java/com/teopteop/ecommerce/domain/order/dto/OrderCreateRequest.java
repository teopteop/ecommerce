package com.teopteop.ecommerce.domain.order.dto;

import com.teopteop.ecommerce.global.common.dto.AddressRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.jspecify.annotations.Nullable;

import java.util.List;

public record OrderCreateRequest(
        @NotEmpty(message = "주문 항목은 최소 1개 이상이어야 합니다.")
        List<OrderItemRequest> items,

        /**
         * 배송지 주소 (선택사항)
         * - null 입력 시 회원의 기본 배송지로 적용
         * - 값 입력 시 @Valid에 의해 AddressRequest 내부의 필드 검증 수행
         */
        @Valid
        @Nullable
        AddressRequest address
) {}
