package com.teopteop.ecommerce.domain.auth.dto;

import com.teopteop.ecommerce.global.common.dto.AddressRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignUpCustomerRequest(
        @NotBlank(message = "이메일은 필수 입력 사항입니다.")
        @Email(message = "유효한 형식의 이메일을 입력해주세요.")
        String email,

        @NotBlank(message = "비밀번호는 필수 입력 사항입니다.")
        @Size(min = 8, max = 64, message = "비밀번호는 8~64자 입니다.")
        @Pattern(
                regexp = "^(?=.*[a-zA-Z])(?=.*\\d).+$",
                message = "비밀번호는 영문자와 숫자를 포함해야 합니다."
        )
        String password,

        @NotBlank(message = "이름은 필수 입력 사항입니다.")
        @Size(max = 30, message = "이름은 30자 이하로 입력해주세요.")
        String name,


        @NotBlank(message = "휴대폰 번호는 필수 입력 사항입니다.")
        @Pattern(
                regexp = "^01[016789]\\d{7,8}$",
                message = "휴대폰 번호는 '-' 없이 숫자만 입력해주세요."
        )
        String phoneNumber,

        @Valid AddressRequest address
) {}
