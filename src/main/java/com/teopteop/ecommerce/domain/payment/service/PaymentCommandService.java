package com.teopteop.ecommerce.domain.payment.service;

import com.teopteop.ecommerce.domain.order.service.OrderQueryService;
import com.teopteop.ecommerce.domain.payment.repository.PaymentJpaRepository;
import com.teopteop.ecommerce.domain.payment.client.TossPaymentClient;
import com.teopteop.ecommerce.domain.payment.dto.*;
import com.teopteop.ecommerce.domain.payment.entity.Payment;
import com.teopteop.ecommerce.domain.payment.entity.PaymentMethod;
import com.teopteop.ecommerce.domain.payment.exception.PaymentErrorCode;
import com.teopteop.ecommerce.global.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PaymentCommandService {

    private final PaymentJpaRepository paymentJpaRepository;

    private final OrderQueryService orderQueryService;

    private final TossPaymentClient tossPaymentClient;

    public void createPayment(Long orderId, String orderNumber, BigDecimal totalAmount) {
        Payment payment = Payment.create(orderId, orderNumber, totalAmount);
        paymentJpaRepository.save(payment);
    }

    public PaymentConfirmResponse confirm(PaymentConfirmRequest request) {
        // 1. orderNumber로 payment 조회 (비관락)
        Payment foundPayment = paymentJpaRepository.findByOrderNumberForUpdate(request.orderNumber())
                .orElseThrow(() -> new ApplicationException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        // 2. 금액 검증 (위변조 방지)
        // 클라이언트가 amount를 조작해서 넘길 수 있기 때문에 DB 금액과 반드시 비교할 것
        if (foundPayment.getTotalAmount().compareTo(request.amount()) != 0) {
            throw new ApplicationException(PaymentErrorCode.INVALID_TOTAL_AMOUNT);
        }

        // 3. PENDING -> IN_PROGRESS
        foundPayment.requestApproval();

        // 4. 토스 API 호출, 실패 시 IN_PROGRESS -> FAILED
        TossConfirmResponse response;
        try {
            response = tossPaymentClient.confirm(
                    new TossConfirmRequest(request.paymentKey(), request.orderNumber(), request.amount())
            );
        } catch (Exception e) {
            foundPayment.reject(); // IN_PROGRESS -> FAILED
            throw e;               // 예외 다시 던지기
        }

        // 5. 결제수단에 따른 상태 전이 분기, 응답 반환
        PaymentMethod paymentMethod = PaymentMethod.from(response.method());

        if (paymentMethod == PaymentMethod.VIRTUAL_ACCOUNT) {
            // 가상계좌: IN_PROGRESS -> WAITING_FOR_DEPOSIT, secret 저장
            foundPayment.waitingForDeposit(response.paymentKey(), paymentMethod, response.secret());
            TossVirtualAccount virtualAccount = response.virtualAccount();

            return new PaymentConfirmResponse.VirtualAccount(
                    foundPayment.getOrderNumber(),
                    foundPayment.getTotalAmount(),
                    virtualAccount.accountNumber(),
                    virtualAccount.bankCode(),
                    virtualAccount.dueDate()
            );
        }

        // 일반결제: IN_PROGRESS -> DONE
        foundPayment.approve(response.paymentKey(), paymentMethod);

        return new PaymentConfirmResponse.Instant(
                foundPayment.getOrderNumber(),
                foundPayment.getTotalAmount(),
                foundPayment.getMethod(),
                foundPayment.getApprovedAt()
        );
    }

    public void handleWebhook(TossPaymentStatusChangedRequest request) {
        String eventType = request.eventType();

        switch (eventType) {
            case "PAYMENT_STATUS_CHANGED" -> handleStatusChanged(request.data());
            case "DEPOSIT_CALLBACK" -> handleDepositCallback(request.data());
            default -> log.warn("처리할 수 없는 웹훅 이벤트: {}", eventType);
        }
    }

    private void handleStatusChanged(TossPaymentData data) {
        Payment foundPayment = paymentJpaRepository.findByOrderNumber(data.orderId())
                .orElseThrow(() -> new ApplicationException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        if (data.status().equals("CANCELED")) {
            foundPayment.cancelFully();
        } else {
            log.warn("처리할 수 없는 상태 변경: {}", data.status());
        }
    }

    // 가상결제 완료
    private void handleDepositCallback(TossPaymentData data) {
        Payment foundPayment = paymentJpaRepository.findByOrderNumber(data.orderId())
                .orElseThrow(() -> new ApplicationException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        foundPayment.completeDeposit();
    }

    // 웹훅 secret 검증


}
