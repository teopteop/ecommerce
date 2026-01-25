package com.teopteop.ecommerce.domain.auth.exception;

import com.teopteop.ecommerce.global.exception.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum UserErrorCode implements BaseErrorCode {
    USERNAME_DUPLICATE(HttpStatus.CONFLICT, "USER_001", "이미 사용중인 아이디입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_002", "사용자를 찾을 수 없습니다.");


    private final HttpStatus status;
    private final String code;
    private final String message;

    UserErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
