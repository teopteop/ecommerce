package com.teopteop.ecommerce.domain.delivery.service;

import com.teopteop.ecommerce.domain.delivery.entity.Delivery;
import com.teopteop.ecommerce.domain.delivery.exception.DeliveryErrorCode;
import com.teopteop.ecommerce.domain.delivery.exception.DeliveryException;
import com.teopteop.ecommerce.domain.delivery.repository.DeliveryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryQueryService {

    private final DeliveryJpaRepository deliveryJpaRepository;

    public List<Delivery> findAllByOrderItemIn(List<Long> orderItemIds) {
        return deliveryJpaRepository.findAllByOrderItemIdIn(orderItemIds);
    }

    public Delivery findById(Long id) {
        return deliveryJpaRepository.findById(id)
                .orElseThrow(() -> new DeliveryException(DeliveryErrorCode.DELIVERY_NOT_FOUND));
    }
}
