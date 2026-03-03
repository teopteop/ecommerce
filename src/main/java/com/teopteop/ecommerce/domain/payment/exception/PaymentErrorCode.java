package com.teopteop.ecommerce.domain.payment.exception;

import com.teopteop.ecommerce.global.exception.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum PaymentErrorCode implements BaseErrorCode {
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 결제를 찾을 수 없습니다."),
    INVALID_STATUS_TRANSITION(HttpStatus.BAD_REQUEST, "잘못된 결제 상태 전이입니다."),
    INVALID_TOTAL_AMOUNT(HttpStatus.BAD_REQUEST, "유효하지 않은 주문 금액입니다."),
    INVALID_CANCEL_AMOUNT(HttpStatus.BAD_REQUEST, "유효하지 않은 취소 금액입니다."),
    CANCEL_AMOUNT_EXCEEDED(HttpStatus.BAD_REQUEST, "취소 요청 금액이 남은 결제 금액을 초과했습니다."),
    PAYMENT_CONFIRM_FAILED(HttpStatus.BAD_GATEWAY, "결제 승인에 실패했습니다."), // 외부 승인 오류 502
    INVALID_PAYMENT_METHOD(HttpStatus.BAD_REQUEST, "지원되지 않는 결제 수단입니다.");

    private final HttpStatus status;
    private final String message;

    PaymentErrorCode(HttpStatus status, String message) {
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
