package com.teopteop.ecommerce.domain.payment.entity;

public enum PaymentStatus {
    PENDING,               // 결제 생성
    IN_PROGRESS,           // 승인 결과 대기
    DONE,                  // 결제 성공
    FAILED,                // 결제 실패
    CANCELED,              // 전액 취소
    PARTIAL_CANCELED,      // 부분 취소
    WAITING_FOR_DEPOSIT,   // 가상계좌 입금 대기
    EXPIRED                // 결제 기한 만료
}
