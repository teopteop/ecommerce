package com.teopteop.ecommerce.domain.delivery.controller;

import com.teopteop.ecommerce.domain.delivery.service.DeliveryFacade;
import com.teopteop.ecommerce.global.common.dto.ApiResponse;
import com.teopteop.ecommerce.global.security.principal.AccountPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seller/deliveries")
@PreAuthorize("hasRole('ROLE_SELLER')")
public class DeliverySellerController {

    private final DeliveryFacade deliveryFacade;

    @PatchMapping("/{id}/ship")
    public ResponseEntity<ApiResponse<Void>> startShipping(
            @PathVariable Long id,
            @AuthenticationPrincipal AccountPrincipal principal
    ) {
        deliveryFacade.startShipping(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<Void>> completeDelivery(
            @PathVariable Long id,
            @AuthenticationPrincipal AccountPrincipal principal
    ) {
        deliveryFacade.completeDelivery(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
