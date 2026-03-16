package com.teopteop.ecommerce.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record BankAccountRequest(

        @NotBlank(message = "은행 코드는 필수 입력 사항입니다.")
        @Pattern(
                regexp = "^\\d{3}$",
                message = "은행 코드는 3자리 숫자로 입력해주세요."
        )
        String bankCode,

        @NotBlank(message = "계좌번호는 필수 입력 사항입니다.")
        @Size(max = 30, message = "계좌번호는 30자 이하로 입력해주세요.")
        String accountNumber,

        @NotBlank(message = "예금주명은 필수 입력 사항입니다.")
        @Size(max = 50, message = "예금주명은 50자 이하로 입력해주세요.")
        String holderName
) {}
