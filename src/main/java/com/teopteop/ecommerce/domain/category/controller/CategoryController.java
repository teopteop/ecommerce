package com.teopteop.ecommerce.domain.category.controller;

import com.teopteop.ecommerce.domain.category.dto.CategoryCreateRequest;
import com.teopteop.ecommerce.domain.category.dto.CategoryCreateResponse;
import com.teopteop.ecommerce.domain.category.dto.CategoryResponse;
import com.teopteop.ecommerce.domain.category.dto.CategoryUpdateRequest;
import com.teopteop.ecommerce.domain.category.service.CategoryService;
import com.teopteop.ecommerce.global.common.dto.ApiResponse;
import com.teopteop.ecommerce.global.common.dto.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryCreateResponse>> createCategory(@Valid @RequestBody CategoryCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(categoryService.registerCategory(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategory(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.findCategory(id)));
    }

    @GetMapping("/{id}/all")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryWithDeleted(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.findCategoryWithDeleted(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CategoryResponse>>> getCategories(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(categoryService.findCategories(pageable))));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateCategory(
            @PathVariable Long id,
            @RequestBody CategoryUpdateRequest request
    ) {
        categoryService.updateCategory(id, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

}
