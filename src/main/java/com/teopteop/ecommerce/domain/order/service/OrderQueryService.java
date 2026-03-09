package com.teopteop.ecommerce.domain.order.service;

import com.teopteop.ecommerce.domain.order.entity.Order;
import com.teopteop.ecommerce.domain.order.exception.OrderErrorCode;
import com.teopteop.ecommerce.domain.order.repository.OrderJpaRepository;
import com.teopteop.ecommerce.global.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderQueryService {

    private final OrderJpaRepository orderJpaRepository;

    public Order findByOrderNumber(String orderNumber) {
        return orderJpaRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ApplicationException(OrderErrorCode.ORDER_NOT_FOUND));
    }
}
