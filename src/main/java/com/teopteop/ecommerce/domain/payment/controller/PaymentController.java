package com.teopteop.ecommerce.domain.payment.controller;

import com.teopteop.ecommerce.domain.payment.dto.PaymentConfirmRequest;
import com.teopteop.ecommerce.domain.payment.dto.PaymentConfirmResponse;
import com.teopteop.ecommerce.domain.payment.service.PaymentCommandService;
import com.teopteop.ecommerce.global.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentCommandService paymentCommandService;

    @PostMapping("/confirm")
    public ResponseEntity<ApiResponse<PaymentConfirmResponse>> confirm(@Valid @RequestBody PaymentConfirmRequest request) {
        return ResponseEntity.ok(ApiResponse.success(paymentCommandService.confirm(request)));
    }
}
