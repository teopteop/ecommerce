package com.teopteop.ecommerce.domain.order.controller;

import com.teopteop.ecommerce.domain.order.dto.OrderCancelRequest;
import com.teopteop.ecommerce.domain.order.dto.OrderCreateRequest;
import com.teopteop.ecommerce.domain.order.dto.OrderCreateResponse;
import com.teopteop.ecommerce.domain.order.dto.OrderPartialCancelRequest;
import com.teopteop.ecommerce.domain.order.service.OrderCommandService;
import com.teopteop.ecommerce.global.common.dto.ApiResponse;
import com.teopteop.ecommerce.global.security.principal.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderCommandService orderCommandService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderCreateResponse>> createOrder(
            @Valid @RequestBody OrderCreateRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(orderCommandService.registerOrder(principal.getId(), request)));
    }

    @DeleteMapping("/cancel/{orderNumber}")
    public ResponseEntity<ApiResponse<Void>> cancelOrder(
            @PathVariable String orderNumber,
            @Valid @RequestBody OrderCancelRequest request
    ) {
        orderCommandService.cancelOrder(orderNumber, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping("/partial-cancel/{orderNumber}")
    public ResponseEntity<ApiResponse<Void>> partialCancelOrder(
            @PathVariable String orderNumber,
            @Valid @RequestBody OrderPartialCancelRequest request
    ) {
       orderCommandService.partialCancelOrder(orderNumber, request);
       return ResponseEntity.ok(ApiResponse.success(null));
    }
}
