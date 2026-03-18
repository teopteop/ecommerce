package com.teopteop.ecommerce.domain.order.service;

import com.teopteop.ecommerce.domain.order.entity.Order;
import com.teopteop.ecommerce.domain.order.exception.OrderErrorCode;
import com.teopteop.ecommerce.domain.order.exception.OrderException;
import com.teopteop.ecommerce.domain.order.repository.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderQueryService {

    private final OrderJpaRepository orderJpaRepository;

    public Order findById(Long id) {
        return orderJpaRepository.findById(id)
                .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND));
    }

    public Order findWithItems(Long id) {
        return orderJpaRepository.findWithItemsById(id)
                .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND));
    }

    public Order findOrderDetail(Long orderId, Long accountId) {
        Order foundOrder = orderJpaRepository.findWithItemsById(orderId)
                .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND));

        if (!foundOrder.getAccountId().equals(accountId)) {
            throw new OrderException(OrderErrorCode.ORDER_FORBIDDEN);
        }

        return foundOrder;
    }

    public Page<Order> findMyOrders(Long accountId, Pageable pageable) {
        return orderJpaRepository.findOrdersByAccountId(accountId, pageable);
    }
}
