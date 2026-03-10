package com.teopteop.ecommerce.domain.order.exception;

import com.teopteop.ecommerce.global.exception.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum OrderErrorCode implements BaseErrorCode {
    INVALID_STATUS_TRANSITION(HttpStatus.BAD_REQUEST, "잘못된 주문 상태 전이입니다."),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 주문을 찾을 수 없습니다."),
    ORDER_FORBIDDEN(HttpStatus.FORBIDDEN, "해당 주문에 접근 권한이 없습니다.");

    private final HttpStatus status;
    private final String message;

    OrderErrorCode (HttpStatus status, String message) {
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
