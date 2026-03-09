package com.teopteop.ecommerce.domain.order.entity;

public enum OrderStatus {
    PENDING,             // 결제 대기
    PAID,                // 결제 완료
    PAYMENT_FAILED,      // 결제 실패
    SHIPPED,             // 배송 시작
    DELIVERED,           // 배송 완료
    PARTIAL_CANCELED,    // 부분취소
    CANCELED             // 취소
}
