package com.teopteop.ecommerce.domain.inventory.controller;

import com.teopteop.ecommerce.domain.inventory.dto.InventoryResponse;
import com.teopteop.ecommerce.domain.inventory.dto.InventoryStockRequest;
import com.teopteop.ecommerce.domain.inventory.service.InventoryService;
import com.teopteop.ecommerce.global.common.dto.ApiResponse;
import com.teopteop.ecommerce.global.common.dto.PageResponse;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inventories")
@Validated
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InventoryResponse>> getInventory(@PathVariable Long id) {
        return ResponseEntity.ok()
                .body(ApiResponse.success(inventoryService.findInventory(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<InventoryResponse>>> getInventories(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok()
                .body(ApiResponse.success(PageResponse.from(inventoryService.findInventories(pageable))));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<InventoryResponse>> getByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.findByProductId(productId)));
    }

    @GetMapping("/under-threshold")
    public ResponseEntity<ApiResponse<PageResponse<InventoryResponse>>> getInventoriesUnderThreshold(
            @RequestParam @Positive int threshold,
            @PageableDefault(size = 10, sort = "quantity", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                PageResponse.from(inventoryService.findInventoriesUnderThreshold(threshold, pageable))));
    }

    @PostMapping("/{id}/deduct")
    public ResponseEntity<ApiResponse<Void>> deduct(
            @PathVariable Long id,
            @RequestBody InventoryStockRequest request
    ) {
        inventoryService.deduct(id, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

}