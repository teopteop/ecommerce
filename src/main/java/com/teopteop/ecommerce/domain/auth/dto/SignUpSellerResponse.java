package com.teopteop.ecommerce.domain.auth.dto;

public record SignUpSellerResponse(
        Long sellerId,
        Long accountId,
        String email
) {}
