package com.teopteop.ecommerce.domain.delivery.repository;

import com.teopteop.ecommerce.domain.delivery.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeliveryJpaRepository extends JpaRepository<Delivery, Long> {

    // OrderItemId 기준 단건 조회
    Optional<Delivery> findByOrderItemId(Long orderItemId);

    // 주문 목록/상세 조회 시 Delivery 매핑용
    List<Delivery> findAllByOrderItemIdIn(List<Long> orderItemIds);
}
