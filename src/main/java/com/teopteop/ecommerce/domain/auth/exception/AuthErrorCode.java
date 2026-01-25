package com.teopteop.ecommerce.domain.auth.exception;

import com.teopteop.ecommerce.global.exception.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum AuthErrorCode implements BaseErrorCode {
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH_001", "아이디 또는 비밀번호가 올바르지 않습니다.");


    private final HttpStatus status;
    private final String code;
    private final String message;

    AuthErrorCode(HttpStatus status, String code, String message) {
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
