package com.teopteop.ecommerce.domain.customer.repository;

import com.teopteop.ecommerce.domain.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerJpaRepository extends JpaRepository<Customer, Long> {

    boolean existsByEmail(String email);

}
