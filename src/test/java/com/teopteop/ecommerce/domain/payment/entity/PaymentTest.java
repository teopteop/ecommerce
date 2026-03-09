package com.teopteop.ecommerce.domain.payment.entity;

import com.teopteop.ecommerce.domain.payment.exception.PaymentErrorCode;
import com.teopteop.ecommerce.global.exception.ApplicationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Payment 엔티티 상태전이 테스트")
class PaymentTest {

    // 초기값 생성 메서드
    private Payment createPayment() {
        return Payment.create(1L, "ORDER-1", BigDecimal.valueOf(10000));
    }

    // DONE 상태 payment 반환
    private Payment createDonePayment() {
        Payment payment = createPayment(); // PENDING
        payment.requestApproval(); // IN_PROGRESS
        payment.approve("key", PaymentMethod.CARD); //DONE

        return payment;
    }

    @Test
    @DisplayName("총 금액이 0 이하이면 예외")
    void invalidAmount() throws Exception {
        assertThatThrownBy(() -> Payment.create(1L, "ORDER-1", BigDecimal.ZERO))
                .isInstanceOfSatisfying(ApplicationException.class, ex -> {
                    assertThat(ex.getErrorCode()).isEqualTo(PaymentErrorCode.INVALID_TOTAL_AMOUNT);
                });
    }

    @Test
    @DisplayName("상태전이 확인 PENDING -> IN_PROGRESS -> DONE")
    void approveSuccess() {
        Payment payment = createPayment();

        // PENDING -> IN_PROGRESS 확인
        payment.requestApproval();
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.IN_PROGRESS);

        // IN_PROGRESS -> DONE 확인
        payment.approve("key", PaymentMethod.CARD);

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.DONE);
        assertThat(payment.getPaymentKey()).isEqualTo("key");
        assertThat(payment.getMethod()).isEqualTo(PaymentMethod.CARD);
        assertThat(payment.getApprovedAt()).isNotNull();
    }

    @Test
    @DisplayName("PENDING 상태에서 approve 호출 시 예외")
    void approveFailsWhenReady() {
        Payment payment = createPayment(); // PENDING

        assertThatThrownBy(() -> payment.approve("key", PaymentMethod.CARD))
                .isInstanceOfSatisfying(ApplicationException.class, ex -> {
                    assertThat(ex.getErrorCode())
                            .isEqualTo(PaymentErrorCode.INVALID_STATUS_TRANSITION);
                });
    }

    @Test
    @DisplayName("DONE 상태에서 requestApproval 호출 시 예외")
    void requestApprovalFailsWhenDone() {
        Payment payment = createDonePayment();

        assertThatThrownBy(payment::requestApproval)
                .isInstanceOfSatisfying(ApplicationException.class, ex -> {
                    assertThat(ex.getErrorCode())
                            .isEqualTo(PaymentErrorCode.INVALID_STATUS_TRANSITION);
                });
    }

    @Test
    @DisplayName("PENDING 상태에서 cancel 호출 시 예외")
    void cancelFullyFailsWhenReady() {
        Payment payment = createPayment();

        assertThatThrownBy(payment::cancelFully)
                .isInstanceOfSatisfying(ApplicationException.class, ex -> {
                    assertThat(ex.getErrorCode())
                            .isEqualTo(PaymentErrorCode.INVALID_STATUS_TRANSITION);
                });
    }

    @Test
    @DisplayName("Done 상태에서 cancelFully 성공 -> CANCELED")
    void cancelFullySuccess() {
        Payment payment = createDonePayment();

        payment.cancelFully();

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.CANCELED);
        assertThat(payment.getCanceledAmount()).isEqualByComparingTo(payment.getTotalAmount());
        assertThat(payment.getCanceledAt()).isNotNull();
    }

    @Test
    @DisplayName("cancelPartially 정상 -> PARTIAL_CANCELED, canceledAmount 누적")
    void cancelPartiallySuccess() {
        Payment payment = createDonePayment();

        payment.cancelPartially(BigDecimal.valueOf(3000));

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PARTIAL_CANCELED);
        assertThat(payment.getCanceledAmount()).isEqualByComparingTo(BigDecimal.valueOf(3000));
    }

    @Test
    @DisplayName("cancelPartially 잔여 금액 초과 시 예외")
    void cancelPartiallyFailsWhenExceeded() {
        Payment payment = createDonePayment();

        assertThatThrownBy(() -> payment.cancelPartially(BigDecimal.valueOf(20000)))
                .isInstanceOfSatisfying(ApplicationException.class, ex -> {
                    assertThat(ex.getErrorCode())
                            .isEqualTo(PaymentErrorCode.CANCEL_AMOUNT_EXCEEDED);
                });
    }

    @Test
    @DisplayName("IN_PROGRESS 상태에서 reject 성공 -> FAILED")
    void rejectSuccess() {
        Payment payment = createPayment();
        payment.requestApproval();

        payment.reject();

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.FAILED);
    }

    @Test
    @DisplayName("FAILED 상태에서 requestApproval 호출 시 -> IN_PROGRESS: 결제 재시도 확인")
    void requestApprovalSuccessWhenFailed() {
        Payment payment = createPayment();
        payment.requestApproval(); // PENDING -> IN_PROGRESS
        payment.reject();          // IN_PROGRESS -> FAILED

        payment.requestApproval(); // FAILED -> IN_PROGRESS (재시도)

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.IN_PROGRESS);
    }
}