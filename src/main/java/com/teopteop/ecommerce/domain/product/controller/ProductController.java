package com.teopteop.ecommerce.domain.product.controller;

import com.teopteop.ecommerce.domain.product.dto.ProductCreateRequest;
import com.teopteop.ecommerce.domain.product.dto.ProductResponse;
import com.teopteop.ecommerce.domain.product.service.ProductService;
import com.teopteop.ecommerce.global.common.dto.ApiResponse;
import com.teopteop.ecommerce.global.common.dto.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody ProductCreateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(productService.registerProduct(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProduct(@RequestParam Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(productService.findProduct(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getProducts(
            @PageableDefault Pageable pageable
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(null);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(@RequestParam Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(null);
    }
}
