package com.teopteop.ecommerce.domain.seller.controller;

import com.teopteop.ecommerce.domain.seller.service.SellerCommandService;
import com.teopteop.ecommerce.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/sellers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminSellerController {

    private final SellerCommandService sellerCommandService;

    @PatchMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<Void>> approveSeller(@PathVariable Long id) {
        sellerCommandService.approveSeller(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
