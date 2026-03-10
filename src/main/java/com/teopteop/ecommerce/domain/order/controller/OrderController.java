package com.teopteop.ecommerce.domain.order.controller;

import com.teopteop.ecommerce.domain.order.dto.*;
import com.teopteop.ecommerce.domain.order.service.OrderCommandService;
import com.teopteop.ecommerce.domain.order.service.OrderQueryService;
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
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderCommandService orderCommandService;
    private final OrderQueryService orderQueryService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderCreateResponse>> createOrder(
            @Valid @RequestBody OrderCreateRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(orderCommandService.registerOrder(principal.getId(), request)));
    }

    @PostMapping("/cancel/{orderNumber}")
    public ResponseEntity<ApiResponse<Void>> cancelOrder(
            @PathVariable String orderNumber,
            @Valid @RequestBody OrderCancelRequest request
    ) {
        orderCommandService.cancelOrder(orderNumber, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/partial-cancel/{orderNumber}")
    public ResponseEntity<ApiResponse<Void>> partialCancelOrder(
            @PathVariable String orderNumber,
            @Valid @RequestBody OrderPartialCancelRequest request
    ) {
       orderCommandService.partialCancelOrder(orderNumber, request);
       return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(ApiResponse.success(orderQueryService.findOrderDetail(id, principal.getMemberId())));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getMyOrders(
            @AuthenticationPrincipal UserPrincipal principal,
            @PageableDefault(page = 0, size = 10, sort = {"createdAt"}, direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.success(orderQueryService.findMyOrders(principal.getMemberId(), pageable)));
    }
}
