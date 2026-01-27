package com.teopteop.ecommerce.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AddressRequest(
   @NotBlank(message = "도시/시는 필수 입력 항목입니다.")
   @Size(max = 50, message = "도시/시는 50자 이하로 작성해주세요.")
   String city,

   @NotBlank(message = "도로명/상세주소는 필수 입력 항목입니다.")
   @Size(max = 100, message = "도로명/상세주소는 100자 이하로 작성해주세요.")
   String street,

   @NotBlank(message = "우편번호는 필수 입력 항목입니다.")
   @Pattern(regexp = "^\\d{5}$", message = "우편번호는 5자리 숫자로 입력해주세요.")
   String zipcode
) {}
