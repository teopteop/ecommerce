package com.teopteop.ecommerce.domain.auth.controller;

import com.teopteop.ecommerce.domain.auth.dto.LoginRequest;
import com.teopteop.ecommerce.domain.auth.dto.LoginResponse;
import com.teopteop.ecommerce.domain.auth.dto.SignUpRequest;
import com.teopteop.ecommerce.domain.auth.dto.SignUpResponse;
import com.teopteop.ecommerce.domain.auth.service.AccountCommandService;
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

    private final AccountCommandService accountCommandService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignUpResponse>> signUp(@Valid @RequestBody SignUpRequest request) {
        SignUpResponse response = accountCommandService.registerAccount(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = accountCommandService.authenticate(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
