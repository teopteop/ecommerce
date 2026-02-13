package com.teopteop.ecommerce.domain.category.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryCreateRequest(
        @NotBlank(message = "카테고리 이름은 필수 입력 사항입니다.")
        String name,

        @NotBlank(message = "카테고리 코드는 필수 입력 사항입니다.")
        String code,

        Long parentId
) {}
