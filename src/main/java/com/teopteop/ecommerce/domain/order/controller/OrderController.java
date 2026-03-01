package com.teopteop.ecommerce.domain.order.controller;

import com.teopteop.ecommerce.domain.order.dto.OrderCreateRequest;
import com.teopteop.ecommerce.domain.order.service.OrderCommandService;
import com.teopteop.ecommerce.global.common.dto.ApiResponse;
import com.teopteop.ecommerce.global.security.principal.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderCommandService orderCommandService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createOrder(
            @Valid @RequestBody OrderCreateRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        orderCommandService.registerOder(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(null));
    }
}
