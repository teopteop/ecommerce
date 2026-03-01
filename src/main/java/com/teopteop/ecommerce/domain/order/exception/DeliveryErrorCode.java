package com.teopteop.ecommerce.domain.order.exception;

import com.teopteop.ecommerce.global.exception.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum DeliveryErrorCode implements BaseErrorCode {
    INVALID_STATUS_TRANSITION(HttpStatus.BAD_REQUEST, "잘못된 배송 상태 전이입니다.");

    private final HttpStatus status;
    private final String message;

    DeliveryErrorCode(HttpStatus status, String message) {
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
