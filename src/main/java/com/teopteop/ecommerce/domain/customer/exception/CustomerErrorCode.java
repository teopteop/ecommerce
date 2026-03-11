package com.teopteop.ecommerce.domain.customer.exception;

import com.teopteop.ecommerce.global.exception.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum CustomerErrorCode implements BaseErrorCode {
    EMAIL_DUPLICATE(HttpStatus.CONFLICT,"이미 사용중인 이메일입니다."),
    CUSTOMER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 고객을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;

    CustomerErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
