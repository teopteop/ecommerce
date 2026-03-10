package com.teopteop.ecommerce.domain.order.repository;

import com.teopteop.ecommerce.domain.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    // 단건 조회: items + delivery fetch join
    @Query("select o from Order o join fetch o.items join fetch o.delivery where o.id = :id")
    Optional<Order> findDetailById(@Param("orderId") Long id);

    // memberId 기반 목록조회
    Page<Order> findByMemberId(Long memberId, Pageable pageable);
}
