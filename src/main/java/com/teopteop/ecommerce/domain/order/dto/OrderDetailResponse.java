package com.teopteop.ecommerce.domain.order.dto;

import com.teopteop.ecommerce.domain.delivery.entity.Delivery;
import com.teopteop.ecommerce.domain.order.entity.Order;
import com.teopteop.ecommerce.domain.order.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record OrderDetailResponse(
        Long orderId,
        OrderStatus status,
        BigDecimal totalPrice,
        LocalDateTime createdAt,
        List<OrderItemResponse> items
) {

    // orderItem 기준으로 OrderItem + Delivery 매핑
    public static OrderDetailResponse from(Order order, List<Delivery> deliveries) {
        Map<Long, Delivery> deliveryMap = deliveries.stream()
                .collect(Collectors.toMap(Delivery::getOrderItemId, d -> d));

        return new OrderDetailResponse(
                order.getId(),
                order.getStatus(),
                order.getTotalPrice(),
                order.getCreatedAt(),
                order.getItems().stream()
                        .map(i -> OrderItemResponse.withDelivery(i, deliveryMap.get(i.getId())))
                        .toList()
        );
    }
}
