package com.teopteop.ecommerce.domain.category.controller;

import com.teopteop.ecommerce.domain.category.dto.CategoryCreateRequest;
import com.teopteop.ecommerce.domain.category.dto.CategoryCreateResponse;
import com.teopteop.ecommerce.domain.category.dto.CategoryResponse;
import com.teopteop.ecommerce.domain.category.dto.CategoryUpdateRequest;
import com.teopteop.ecommerce.domain.category.service.CategoryCommandService;
import com.teopteop.ecommerce.domain.category.service.CategoryQueryService;
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
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryCommandService categoryCommandService;
    private final CategoryQueryService categoryQueryService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategory(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(categoryQueryService.findCategory(id)));
    }

    @GetMapping("/{id}/all")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryWithDeleted(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(categoryQueryService.findCategoryWithDeleted(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CategoryResponse>>> getCategories(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(categoryQueryService.findCategories(pageable))));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryCreateResponse>> createCategory(@Valid @RequestBody CategoryCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(categoryCommandService.registerCategory(request)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateCategory(
            @PathVariable Long id,
            @RequestBody CategoryUpdateRequest request
    ) {
        categoryCommandService.updateCategory(id, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        categoryCommandService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

}
