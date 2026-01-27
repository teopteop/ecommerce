package com.teopteop.ecommerce.global.security.jwt;

import com.teopteop.ecommerce.global.exception.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum JwtErrorCode implements BaseErrorCode {
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    TOKEN_UNSUPPORTED(HttpStatus.UNAUTHORIZED, "지원되지 않는 토큰입니다."),
    TOKEN_MALFORMED(HttpStatus.UNAUTHORIZED, "잘못된 형식의 토큰입니다."),
    TOKEN_SIGNATURE_INVALID(HttpStatus.UNAUTHORIZED, "토큰 서명이 유효하지 않습니다."),
    TOKEN_EMPTY(HttpStatus.BAD_REQUEST, "토큰이 존재하지 않습니다."),
    SECRET_KEY_INVALID(HttpStatus.INTERNAL_SERVER_ERROR, "JWT 시크릿 키가 잘못되었습니다."),
    USER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "토큰에 담긴 사용자 정보를 DB에서 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;

    JwtErrorCode(HttpStatus status,  String message) {
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
