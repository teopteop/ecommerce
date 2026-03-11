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

    // 단건 조회: OrderItem, Delivery fetch join
    @Query("select o from Order o join fetch o.items join fetch o.delivery where o.id = :id")
    Optional<Order> findWithItemsAndDeliveryById(@Param("id") Long id);

    // customerId 기반 목록조회: Delivery fetch join, 카운트 쿼리 명시
    @Query(
            value = "select o from Order o join fetch o.delivery where o.customerId = :customerId",
            countQuery = "select count(o) from Order o where o.customerId = :customerId"
    )
    Page<Order> findOrdersByCustomerId(@Param("customerId") Long customerId, Pageable pageable);
}
