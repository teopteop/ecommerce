package com.teopteop.ecommerce.domain.payment.entity;

import com.teopteop.ecommerce.domain.payment.exception.PaymentErrorCode;
import com.teopteop.ecommerce.domain.payment.exception.PaymentException;

public enum PaymentMethod {
    CARD,                   // 카드
    VIRTUAL_ACCOUNT,        // 가상계좌
    TRANSFER,               // 계좌이체
    MOBILE_PHONE,           // 휴대폰
    EASY_PAY,               // 간편결제
    GIFT_CERTIFICATE,       // 문화상품권
    BOOK_GIFT_CERTIFICATE,  // 도서문화상품권
    GAME_GIFT_CERTIFICATE;  // 게임문화상품권

    public static PaymentMethod from(String tossMethod) {
        if (tossMethod == null) {
            throw new PaymentException(PaymentErrorCode.INVALID_PAYMENT_METHOD);
        }

        return switch (tossMethod) {
            case "카드" -> CARD;
            case "가상계좌" -> VIRTUAL_ACCOUNT;
            case "계좌이체" -> TRANSFER;
            case "휴대폰" -> MOBILE_PHONE;
            case "간편결제" -> EASY_PAY;
            case "문화상품권" -> GIFT_CERTIFICATE;
            case "도서문화상품권" -> BOOK_GIFT_CERTIFICATE;
            case "게임문화상품권" -> GAME_GIFT_CERTIFICATE;
            default -> throw new PaymentException(PaymentErrorCode.INVALID_PAYMENT_METHOD);
        };
    }
}
