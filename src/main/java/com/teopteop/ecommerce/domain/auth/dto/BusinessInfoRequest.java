package com.teopteop.ecommerce.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record BusinessInfoRequest(

        @NotBlank(message = "상호명은 필수 입력 사항입니다.")
        @Size(max = 100, message = "상호명은 100자 이하로 입력해주세요.")
        String businessName,

        @NotBlank(message = "사업자 등록번호는 필수 입력 사항입니다.")
        @Pattern(
                regexp = "^\\d{10}$",
                message = "사업자 등록번호는 10자리 숫자로 입력해주세요."
        )
        String businessNumber,

        @NotBlank(message = "대표자명은 필수 입력 사항입니다.")
        @Size(max = 50, message = "대표자명은 50자 이하로 입력해주세요.")
        String representativeName
) {}
