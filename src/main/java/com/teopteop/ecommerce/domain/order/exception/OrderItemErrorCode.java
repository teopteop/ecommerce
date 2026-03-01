package com.teopteop.ecommerce.domain.order.exception;

import com.teopteop.ecommerce.global.exception.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum OrderItemErrorCode implements BaseErrorCode {
    ORDER_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 주문 항목을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;

    OrderItemErrorCode(HttpStatus status, String message) {
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
