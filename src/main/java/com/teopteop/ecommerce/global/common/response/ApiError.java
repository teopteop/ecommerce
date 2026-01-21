package com.teopteop.ecommerce.global.common.response;

import lombok.Getter;

@Getter
public class ApiError {
    private final String errorCode;
    private final String errorMessage;

    private ApiError(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public static ApiError of(String errorCode, String errorMessage) {
        return new ApiError(errorCode, errorMessage);
    }
}
