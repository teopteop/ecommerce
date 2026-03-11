package com.teopteop.ecommerce.domain.auth.dto;

public record SignUpCustomerResponse(
        Long id,
        Long accountId,
        String username
) {}
