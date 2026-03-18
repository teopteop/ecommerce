package com.teopteop.ecommerce.domain.delivery.service;

import com.teopteop.ecommerce.domain.delivery.entity.Delivery;
import com.teopteop.ecommerce.domain.delivery.exception.DeliveryErrorCode;
import com.teopteop.ecommerce.domain.delivery.exception.DeliveryException;
import com.teopteop.ecommerce.domain.delivery.repository.DeliveryJpaRepository;
import com.teopteop.ecommerce.domain.order.entity.OrderItem;
import com.teopteop.ecommerce.global.common.vo.Address;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryCommandService {

    private final DeliveryJpaRepository deliveryJpaRepository;

    // OrderItem : Delivery = 1:1 매핑
    public void registerForOrderItem(List<OrderItem> items, String receiverName, String phoneNumber, Address address) {
        items.forEach(item -> deliveryJpaRepository.save(
                Delivery.create(item.getId(), item.getSellerId(), receiverName, phoneNumber, address))
        );
    }

    public void startShipping(Long id) {
        Delivery foundDelivery = deliveryJpaRepository.findById(id)
                .orElseThrow(() -> new DeliveryException(DeliveryErrorCode.DELIVERY_NOT_FOUND));
        foundDelivery.ship();
    }

    public void completeDelivery(Long id) {
        Delivery foundDelivery = deliveryJpaRepository.findById(id)
                .orElseThrow(() -> new DeliveryException(DeliveryErrorCode.DELIVERY_NOT_FOUND));
        foundDelivery.complete();
    }
}
