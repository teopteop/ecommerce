package com.teopteop.ecommerce.domain.order.service;

import com.teopteop.ecommerce.domain.order.dto.OrderCreateRequest;
import com.teopteop.ecommerce.domain.order.dto.OrderCreateResponse;
import com.teopteop.ecommerce.domain.order.repository.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderCommandService {

    private final OrderJpaRepository orderJpaRepository;

    public OrderCreateResponse registerOder(Long userId, OrderCreateRequest request) {
        return null;
    }
}
