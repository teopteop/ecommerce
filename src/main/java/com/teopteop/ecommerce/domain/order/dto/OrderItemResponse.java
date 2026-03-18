package com.teopteop.ecommerce.domain.order.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.teopteop.ecommerce.domain.delivery.dto.DeliveryResponse;
import com.teopteop.ecommerce.domain.delivery.entity.Delivery;
import com.teopteop.ecommerce.domain.delivery.entity.DeliveryStatus;
import com.teopteop.ecommerce.domain.order.entity.OrderItem;
import com.teopteop.ecommerce.domain.order.entity.OrderItemStatus;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OrderItemResponse(
        Long id,
        Long productId,
        String productName,
        BigDecimal orderPrice,
        int quantity,
        OrderItemStatus status,
        DeliveryStatus deliveryStatus,
        DeliveryResponse delivery
) {
    // 목록 조회용: 배송상태 포함 OrderSummaryResponse에서 사용
    public static OrderItemResponse withDeliveryStatus(OrderItem item, Delivery delivery) {
        return new OrderItemResponse(
                item.getId(),
                item.getProductId(),
                item.getProductName(),
                item.getOrderPrice(),
                item.getQuantity(),
                item.getStatus(),
                delivery.getStatus(),
                null
        );
    }

    // 단건 조회용: 배송정보를 OrderDetailResponse에서 조합
    public static OrderItemResponse withDelivery(OrderItem item, Delivery delivery) {
        return new OrderItemResponse(
                item.getId(),
                item.getProductId(),
                item.getProductName(),
                item.getOrderPrice(),
                item.getQuantity(),
                item.getStatus(),
                null,
                DeliveryResponse.fromEntity(delivery)
        );
    }
}
