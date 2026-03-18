package com.teopteop.ecommerce.domain.delivery.exception;

import com.teopteop.ecommerce.global.exception.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum DeliveryErrorCode implements BaseErrorCode {
    INVALID_STATUS_TRANSITION(HttpStatus.BAD_REQUEST, "잘못된 배송 상태 전이입니다."),
    DELIVERY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 배송을 찾을 수 없습니다."),
    DELIVERY_FORBIDDEN(HttpStatus.FORBIDDEN, "해당 배송에 접근 권한이 없습니다.");

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
