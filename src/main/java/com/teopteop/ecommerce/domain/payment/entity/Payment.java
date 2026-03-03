package com.teopteop.ecommerce.domain.payment.entity;

import com.teopteop.ecommerce.domain.payment.exception.PaymentErrorCode;
import com.teopteop.ecommerce.global.common.entity.BaseTimeEntity;
import com.teopteop.ecommerce.global.exception.ApplicationException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Payment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orderId; // 내부 조인용

    @Column(name = "order_number", nullable = false)
    private String orderNumber; // Order 외부 식별용 UUID

    @Column(name = "payment_key", unique = true)
    private String paymentKey; // 토스 paymentKey

    @Column(name = "total_amount", nullable = false, precision = 19, scale = 0)
    private BigDecimal totalAmount;

    @Column(name = "canceled_amount", nullable = false, precision = 19, scale = 0)
    private BigDecimal canceledAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    private Payment(
            Long orderId,
            String orderNumber,
            BigDecimal totalAmount
    ) {
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ApplicationException(PaymentErrorCode.INVALID_TOTAL_AMOUNT);
        }

        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.totalAmount = totalAmount;
        this.canceledAmount = BigDecimal.ZERO;
        this.status = PaymentStatus.READY;
    }

    public static Payment create(
            Long orderId,
            String orderNumber,
            BigDecimal totalAmount
    ) {
        return new Payment(orderId, orderNumber, totalAmount);
    }

    // === 상태 전이 메서드 ===
    // READY -> PENDING -> DONE or FAILED
    public void requestApproval() {
        if (this.status != PaymentStatus.READY) {
            throw new ApplicationException(PaymentErrorCode.INVALID_STATUS_TRANSITION);
        }

        this.status = PaymentStatus.PENDING;
    }

    public void approve(String paymentKey, PaymentMethod method) {
        if (this.status != PaymentStatus.PENDING) {
            throw new ApplicationException(PaymentErrorCode.INVALID_STATUS_TRANSITION);
        }

        this.paymentKey = paymentKey;
        this.method = method;
        this.status = PaymentStatus.DONE;
        this.approvedAt = LocalDateTime.now();
    }

    public void reject() {
        if (this.status != PaymentStatus.PENDING) {
            throw new ApplicationException(PaymentErrorCode.INVALID_STATUS_TRANSITION);
        }

        this.status = PaymentStatus.FAILED;
    }

    public void cancelFully() {
        if (this.status != PaymentStatus.DONE
                && this.status != PaymentStatus.PARTIAL_CANCELED) { // 부분 취소 상태에서도 전체 취소 가능
            throw new ApplicationException(PaymentErrorCode.INVALID_STATUS_TRANSITION);
        }

        this.canceledAmount = this.totalAmount;
        this.status = PaymentStatus.CANCELED;
        this.canceledAt = LocalDateTime.now();
    }

    public void cancelPartially(BigDecimal amount) {
        if (this.status != PaymentStatus.DONE
                && this.status != PaymentStatus.PARTIAL_CANCELED) { // 부분 취소 상태에서도 추가로 취소 가능
            throw new ApplicationException(PaymentErrorCode.INVALID_STATUS_TRANSITION);
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ApplicationException(PaymentErrorCode.INVALID_CANCEL_AMOUNT);
        }

        // 남은 금액 = 총 금액 - 누적 취소 금액
        BigDecimal remaining = this.totalAmount.subtract(this.canceledAmount);

        if (remaining.compareTo(amount) < 0) {
            throw new ApplicationException(PaymentErrorCode.CANCEL_AMOUNT_EXCEEDED);
        }

        // 취소 금액 += 입력값(취소된 OrderItem.orderPrice)
        this.canceledAmount = this.canceledAmount.add(amount);

        /*
         * 부분 취소에 따른 상태 전이
         * 1. 총 금액과 같다 -> 전체 취소
         * 2. 총 금액과 다르다 -> 부분 취소
         */
        if (this.canceledAmount.compareTo(this.totalAmount) == 0) {
            this.status = PaymentStatus.CANCELED;
        } else {
            this.status = PaymentStatus.PARTIAL_CANCELED;
        }

        this.canceledAt = LocalDateTime.now();
    }

}
