package com.teopteop.ecommerce.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "이메일을 입력하세요.")
        @Email(message = "유효한 형식의 이메일을 입력해주세요.")
        String email,

        @NotBlank(message = "비밀번호를 입력하세요.")
        String password
) {}
