package com.teopteop.ecommerce.domain.order.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.teopteop.ecommerce.domain.order.entity.DeliveryStatus;
import com.teopteop.ecommerce.domain.order.entity.Order;
import com.teopteop.ecommerce.domain.order.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OrderResponse(
        String orderNumber,
        OrderStatus status,
        BigDecimal totalPrice,
        LocalDateTime createdAt,
        List<OrderItemResponse> items,
        DeliveryStatus deliveryStatus,
        DeliveryResponse delivery
) {
    // 단건 조회용: DeliveryResponse 포함
    public static OrderResponse ofDetail(Order order) {
        return new OrderResponse(
                order.getOrderNumber(),
                order.getStatus(),
                order.getTotalPrice(),
                order.getCreatedAt(),
                order.getItems().stream()
                        .map(OrderItemResponse::fromEntity).toList(),
                null,
                DeliveryResponse.fromEntity(order.getDelivery())
        );
    }

    // 목록 조회용: DeliveryStatus 포함
    public static OrderResponse ofSummary(Order order) {
        return new OrderResponse(
                order.getOrderNumber(),
                order.getStatus(),
                order.getTotalPrice(),
                order.getCreatedAt(),
                order.getItems().stream()
                        .map(OrderItemResponse::fromEntity).toList(),
                order.getDelivery().getStatus(),
                null
        );
    }
}
