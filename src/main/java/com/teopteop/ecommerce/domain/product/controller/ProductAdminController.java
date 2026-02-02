package com.teopteop.ecommerce.domain.product.controller;

import com.teopteop.ecommerce.domain.auth.entity.UserRole;
import com.teopteop.ecommerce.domain.product.dto.ProductAdminCreateRequest;
import com.teopteop.ecommerce.domain.product.dto.ProductAdminCreateResponse;
import com.teopteop.ecommerce.domain.product.dto.ProductAdminResponse;
import com.teopteop.ecommerce.domain.product.dto.ProductAdminUpdateRequest;
import com.teopteop.ecommerce.domain.product.service.ProductCommandService;
import com.teopteop.ecommerce.domain.product.service.ProductQueryService;
import com.teopteop.ecommerce.global.common.dto.ApiResponse;
import com.teopteop.ecommerce.global.common.dto.PageResponse;
import com.teopteop.ecommerce.global.security.principal.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
            @RequestParam Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        UserRole role = principal.getRole();
        return ResponseEntity.ok(ApiResponse.success(productQueryService.findProductWithDeleted(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProductAdminResponse>>> getProductsWithDeleted(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        UserRole role = principal.getRole();
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(productQueryService.findProductsWithDeleted(pageable))));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductAdminResponse>> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductAdminUpdateRequest request
    ) {
        productCommandService.updateProduct(id, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success(null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@RequestParam Long id) {
        productCommandService.markProductDeleted(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success(null));
    }
}
