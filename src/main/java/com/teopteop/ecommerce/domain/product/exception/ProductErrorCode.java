package com.teopteop.ecommerce.domain.product.exception;

import com.teopteop.ecommerce.global.exception.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum ProductErrorCode implements BaseErrorCode {
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 상품을 찾을 수 없습니다."),
    INVALID_UPDATE_REQUEST(HttpStatus.BAD_REQUEST, "수정 요청에는 최소 하나 이상의 변경 값이 필요합니다");

    private final HttpStatus status;
    private final String message;

    ProductErrorCode(HttpStatus status, String message) {
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
