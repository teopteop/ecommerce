package com.teopteop.ecommerce.domain.payment.service;

import com.teopteop.ecommerce.domain.payment.Repository.PaymentJpaRepository;
import com.teopteop.ecommerce.domain.payment.client.TossPaymentClient;
import com.teopteop.ecommerce.domain.payment.dto.PaymentConfirmRequest;
import com.teopteop.ecommerce.domain.payment.dto.PaymentConfirmResponse;
import com.teopteop.ecommerce.domain.payment.dto.TossConfirmRequest;
import com.teopteop.ecommerce.domain.payment.dto.TossConfirmResponse;
import com.teopteop.ecommerce.domain.payment.entity.Payment;
import com.teopteop.ecommerce.domain.payment.entity.PaymentMethod;
import com.teopteop.ecommerce.domain.payment.exception.PaymentErrorCode;
import com.teopteop.ecommerce.global.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentCommandService {

    private final PaymentJpaRepository paymentJpaRepository;
    private final TossPaymentClient tossPaymentClient;

    public PaymentConfirmResponse confirm(PaymentConfirmRequest request) {
        // 1. orderNumber로 payment 조회 (비관락)
        Payment foundPayment = paymentJpaRepository.findByOrderNumberForUpdate(request.orderNumber())
                .orElseThrow(() -> new ApplicationException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        // 2. 금액 검증 (위변조 방지)
        // 클라이언트가 amount를 조작해서 넘길 수 있기 때문에 DB 금액과 반드시 비교할 것
        if (foundPayment.getTotalAmount().compareTo(request.amount()) != 0) {
            throw new ApplicationException(PaymentErrorCode.INVALID_TOTAL_AMOUNT);
        }

        // 3. READY -> PENDING
        foundPayment.requestApproval();

        // 4. 토스 API 호출, 실패 시 PENDING -> FAILED
        TossConfirmResponse response;
        try {
            response = tossPaymentClient.confirm(
                    new TossConfirmRequest(request.paymentKey(), request.orderNumber(), request.amount())
            );
        } catch (Exception e) {
            foundPayment.reject(); // PENDING -> FAILED
            throw e;               // 예외 다시 던지기
        }

        // 5. PENDING -> DONE
        PaymentMethod paymentMethod = PaymentMethod.from(response.method());
        foundPayment.approve(response.paymentKey(), paymentMethod);

        // 6. 응답 반환
        return new PaymentConfirmResponse(
                foundPayment.getOrderNumber(),
                foundPayment.getTotalAmount(),
                foundPayment.getMethod(),
                foundPayment.getApprovedAt()
        );
    }

}
