package com.teopteop.ecommerce.domain.customer.service;

import com.teopteop.ecommerce.domain.customer.entity.Customer;
import com.teopteop.ecommerce.domain.customer.repository.CustomerJpaRepository;
import com.teopteop.ecommerce.global.common.vo.Address;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerCommandService {

    private final CustomerJpaRepository customerJpaRepository;

    public Customer registerCustomer(Long accountId, String name, String phoneNumber, Address address) {
        return customerJpaRepository.save(
                Customer.create(
                        accountId,
                        name,
                        phoneNumber,
                        address
                )
        );
    }

}