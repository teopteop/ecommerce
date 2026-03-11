package com.teopteop.ecommerce.domain.order.service;

import com.teopteop.ecommerce.domain.order.dto.OrderResponse;
import com.teopteop.ecommerce.domain.order.entity.Order;
import com.teopteop.ecommerce.domain.order.exception.OrderErrorCode;
import com.teopteop.ecommerce.domain.order.exception.OrderException;
import com.teopteop.ecommerce.domain.order.repository.OrderJpaRepository;
import com.teopteop.ecommerce.global.common.dto.PageResponse;
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

    /**
     * 타 도메인(Payment 등)에서 상태 전이 목적으로 사용
     * items, delivery 조회가 필요한 경우 fetch join 메서드를 사용할 것
     */
    public Order findById(Long id) {
        return orderJpaRepository.findById(id)
                .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND));
    }

    public OrderResponse findOrderDetail(Long orderId, Long customerId) {
        Order foundOrder = orderJpaRepository.findWithItemsAndDeliveryById(orderId)
                .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND));

        if (!foundOrder.getCustomerId().equals(customerId)) {
            throw new OrderException(OrderErrorCode.ORDER_FORBIDDEN);
        }

        return OrderResponse.ofDetail(foundOrder);
    }

    public PageResponse<OrderResponse> findMyOrders(Long customerId, Pageable pageable) {
        Page<Order> foundOrders = orderJpaRepository.findOrdersByCustomerId(customerId, pageable);

        return PageResponse.from(foundOrders.map(OrderResponse::ofSummary));
    }
}
