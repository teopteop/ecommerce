package com.teopteop.ecommerce.domain.member.exception;

import com.teopteop.ecommerce.global.exception.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum MemberErrorCode implements BaseErrorCode {
    EMAIL_DUPLICATE(HttpStatus.CONFLICT, "MEMBER_001", "이미 사용중인 이메일입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    MemberErrorCode(HttpStatus status, String code, String message) {
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
