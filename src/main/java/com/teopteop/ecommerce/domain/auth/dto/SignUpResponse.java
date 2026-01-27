package com.teopteop.ecommerce.domain.auth.dto;

public record SignUpResponse(
        Long userId,
        String username
) {}
