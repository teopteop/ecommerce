package com.teopteop.ecommerce.domain.customer.repository;

import com.teopteop.ecommerce.domain.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerJpaRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByAccountId(Long accountId);

}
