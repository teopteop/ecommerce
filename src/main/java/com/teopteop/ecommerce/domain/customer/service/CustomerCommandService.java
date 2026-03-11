package com.teopteop.ecommerce.domain.customer.service;

import com.teopteop.ecommerce.domain.customer.entity.Customer;
import com.teopteop.ecommerce.domain.customer.repository.CustomerJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerCommandService {

    private final CustomerJpaRepository customerJpaRepository;

    public Customer registerCustomer(Customer customer) {
        return customerJpaRepository.save(customer);
    }

}