package com.teopteop.ecommerce.domain.auth.controller;

import com.teopteop.ecommerce.domain.auth.dto.*;
import com.teopteop.ecommerce.domain.auth.service.AuthFacade;
import com.teopteop.ecommerce.global.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthFacade authFacade;

    @PostMapping("/signup-customer")
    public ResponseEntity<ApiResponse<SignUpCustomerResponse>> signUpCustomer(@Valid @RequestBody SignUpCustomerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(authFacade.registerCustomerAccount(request)));
    }

    @PostMapping("/signup-seller")
    public ResponseEntity<ApiResponse<SignUpSellerResponse>> signUpSeller(@Valid @RequestBody SignUpSellerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(authFacade.registerSellerAccount(request)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authFacade.authenticate(request)));
    }
}
