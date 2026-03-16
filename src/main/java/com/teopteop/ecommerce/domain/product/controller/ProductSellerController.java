package com.teopteop.ecommerce.domain.product.controller;

import com.teopteop.ecommerce.domain.product.dto.ProductCreateRequest;
import com.teopteop.ecommerce.domain.product.dto.ProductCreateResponse;
import com.teopteop.ecommerce.domain.product.service.ProductCommandService;
import com.teopteop.ecommerce.global.common.dto.ApiResponse;
import com.teopteop.ecommerce.global.security.principal.AccountPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seller/products")
@PreAuthorize("hasRole('ROLE_SELLER')")
public class ProductSellerController {

    private final ProductCommandService productCommandService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductCreateResponse>> createProduct(
            @AuthenticationPrincipal AccountPrincipal principal,
            @Valid @RequestBody ProductCreateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(productCommandService.registerProduct(principal.getId(), request)));
    }
}
