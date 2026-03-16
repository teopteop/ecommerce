package com.teopteop.ecommerce.domain.seller.exception;

import com.teopteop.ecommerce.global.exception.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum SellerErrorCode implements BaseErrorCode {
    INVALID_CODE(HttpStatus.BAD_REQUEST, "유효하지 않은 은행 코드입니다."),
    SELLER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 판매자를 찾을 수 없습니다."),
    INVALID_STATUS_TRANSITION(HttpStatus.BAD_REQUEST, "잘못된 판매자 상태 전이입니다.");

    private final HttpStatus status;
    private final String message;

    SellerErrorCode(HttpStatus status, String message) {
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
