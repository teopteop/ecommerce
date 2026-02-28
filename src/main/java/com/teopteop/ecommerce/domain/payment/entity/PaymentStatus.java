package com.teopteop.ecommerce.domain.payment.entity;

public enum PaymentStatus {
    READY,            // 결제 생성
    PENDING,          // 승인 결과 대기
    DONE,             // 결제 성공
    FAILED,           // 결제 실패
    CANCELED,         // 전액 취소
    PARTIAL_CANCELED  // 부분 취소
}
