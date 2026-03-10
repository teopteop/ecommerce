package com.teopteop.ecommerce.domain.order.dto;

import com.teopteop.ecommerce.domain.order.entity.OrderItem;
import com.teopteop.ecommerce.domain.order.entity.OrderItemStatus;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long productId,
        BigDecimal orderPrice,
        int quantity,
        OrderItemStatus status
) {
    public static OrderItemResponse fromEntity(OrderItem item) {
        return new OrderItemResponse(
                item.getProductId(),
                item.getOrderPrice(),
                item.getQuantity(),
                item.getStatus()
        );
    }
}
