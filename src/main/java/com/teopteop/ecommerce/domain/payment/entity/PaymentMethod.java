package com.teopteop.ecommerce.domain.payment.entity;

public enum PaymentMethod {
    CARD,              // 신용카드
    VIRTUAL_ACCOUNT,   // 가상계좌
    TRANSFER,          // 계좌이체
    MOBILE_PHONE,      // 휴대폰 결제
    EASY_PAY           // 간편결제
}
