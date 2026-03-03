package com.teopteop.ecommerce.domain.product.controller;

import com.teopteop.ecommerce.domain.product.dto.ProductAdminCreateRequest;
import com.teopteop.ecommerce.domain.product.dto.ProductAdminCreateResponse;
import com.teopteop.ecommerce.domain.product.dto.ProductAdminResponse;
import com.teopteop.ecommerce.domain.product.dto.ProductAdminUpdateRequest;
import com.teopteop.ecommerce.domain.product.service.ProductCommandService;
import com.teopteop.ecommerce.domain.product.service.ProductQueryService;
import com.teopteop.ecommerce.global.common.dto.ApiResponse;
import com.teopteop.ecommerce.global.common.dto.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/products")
public class ProductAdminController {

    private final ProductCommandService productCommandService;
    private final ProductQueryService productQueryService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductAdminCreateResponse>> createProduct(
            @Valid @RequestBody ProductAdminCreateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(productCommandService.registerProduct(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductAdminResponse>> getProductWithDeleted(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(ApiResponse.success(productQueryService.findProductWithDeleted(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProductAdminResponse>>> getProductsWithDeleted(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(PageResponse.from(productQueryService.findProductsWithDeleted(pageable)))
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductAdminUpdateRequest request
    ) {
        productCommandService.updateProduct(id, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productCommandService.markProductDeleted(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
