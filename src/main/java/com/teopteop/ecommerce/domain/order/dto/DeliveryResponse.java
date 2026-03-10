package com.teopteop.ecommerce.domain.order.dto;

import com.teopteop.ecommerce.domain.order.entity.Delivery;
import com.teopteop.ecommerce.domain.order.entity.DeliveryStatus;
import com.teopteop.ecommerce.global.common.vo.Address;

public record DeliveryResponse(
        String receiverName,
        String phoneNumber,
        Address address,
        DeliveryStatus status
) {
    public static DeliveryResponse fromEntity(Delivery delivery) {
        return new DeliveryResponse(
                delivery.getReceiverName(),
                delivery.getPhoneNumber(),
                delivery.getAddress(),
                delivery.getStatus()
        );
    }
}
