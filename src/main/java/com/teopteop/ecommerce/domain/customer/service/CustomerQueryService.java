package com.teopteop.ecommerce.domain.customer.service;

import com.teopteop.ecommerce.domain.customer.entity.Customer;
import com.teopteop.ecommerce.domain.customer.exception.CustomerErrorCode;
import com.teopteop.ecommerce.domain.customer.exception.CustomerException;
import com.teopteop.ecommerce.domain.customer.repository.CustomerJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerQueryService {

    private final CustomerJpaRepository customerJpaRepository;

    public Customer findById(Long id) {
        return customerJpaRepository.findById(id)
                .orElseThrow(() -> new CustomerException(CustomerErrorCode.CUSTOMER_NOT_FOUND));
    }

    public void validateEmailNotDuplicate(String email) {
        if (customerJpaRepository.existsByEmail(email)) {
            throw new CustomerException(CustomerErrorCode.EMAIL_DUPLICATE);
        }
    }

}