package com.teopteop.ecommerce.domain.payment.controller;

import com.teopteop.ecommerce.domain.payment.dto.PaymentConfirmRequest;
import com.teopteop.ecommerce.domain.payment.dto.PaymentConfirmResponse;
import com.teopteop.ecommerce.domain.payment.dto.TossDepositCallbackRequest;
import com.teopteop.ecommerce.domain.payment.dto.TossPaymentStatusChangedRequest;
import com.teopteop.ecommerce.domain.payment.service.PaymentCommandService;
import com.teopteop.ecommerce.global.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentCommandService paymentCommandService;

    @PostMapping("/confirm")
    public ResponseEntity<ApiResponse<PaymentConfirmResponse>> confirm(@Valid @RequestBody PaymentConfirmRequest request) {
        return ResponseEntity.ok(ApiResponse.success(paymentCommandService.confirm(request)));
    }

    @PostMapping("/webhook/payment")
    public ResponseEntity<ApiResponse<Void>> paymentWebhook(@RequestBody TossPaymentStatusChangedRequest request) {
        paymentCommandService.handleStatusChanged(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/webhook/deposit")
    public ResponseEntity<ApiResponse<Void>> depositWebhook(@RequestBody TossDepositCallbackRequest request) {
        paymentCommandService.handleDepositCallback(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
