package com.teopteop.ecommerce.domain.order.repository;

import com.teopteop.ecommerce.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {
}
