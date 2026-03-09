package com.teopteop.ecommerce.domain.payment.service;

import com.teopteop.ecommerce.domain.order.entity.Order;
import com.teopteop.ecommerce.domain.order.entity.OrderCancelReason;
import com.teopteop.ecommerce.domain.order.service.OrderQueryService;
import com.teopteop.ecommerce.domain.payment.entity.PaymentStatus;
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
        // 1. Payment 조회 (비관락)
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
            foundPayment.reject();           // Payment: IN_PROGRESS -> FAILED

            Order foundOrder = orderQueryService.findByOrderNumber(request.orderNumber());
            foundOrder.markPaymentFailed();  // Order: PENDING -> PAYMENT_FAILED
            throw e;
        }

        // 5. 결제수단에 따른 상태 전이 분기, 응답 반환
        PaymentMethod paymentMethod = PaymentMethod.from(response.method());

        if (paymentMethod == PaymentMethod.VIRTUAL_ACCOUNT) {
            // 가상계좌: IN_PROGRESS -> WAITING_FOR_DEPOSIT, secret 저장
            // Order는 상태전이 없이 입금 완료 시 전송되는 웹훅으로 처리함
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

        Order foundOrder = orderQueryService.findByOrderNumber(request.orderNumber());
        foundOrder.markPaid(); // Order: PENDING -> PAID

        return new PaymentConfirmResponse.Instant(
                foundPayment.getOrderNumber(),
                foundPayment.getTotalAmount(),
                foundPayment.getMethod(),
                foundPayment.getApprovedAt()
        );
    }

    public void cancelPayment(String orderNumber, OrderCancelReason cancelReason, BigDecimal cancelAmount) {
        // 1. orderNumber로 Payment 조회 (비관락)
        Payment foundPayment = paymentJpaRepository.findByOrderNumberForUpdate(orderNumber)
                .orElseThrow(() -> new ApplicationException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        // 2. 전액, 부분 취소 분기: 토스 API 호출 -> DB 상태 전이
        if (cancelAmount == null) {
            // 전액 취소
            tossPaymentClient.cancelFully(foundPayment.getPaymentKey(), cancelReason.getDescription());
            foundPayment.cancelFully();
        } else {
            // 부분 취소
            tossPaymentClient.cancelPartially(foundPayment.getPaymentKey(), cancelReason.getDescription(), cancelAmount);
            foundPayment.cancelPartially(cancelAmount);
        }
    }

    public void handleWebhook(String secret, TossPaymentStatusChangedRequest request) {
        String eventType = request.eventType();

        switch (eventType) {
            case "PAYMENT_STATUS_CHANGED" -> handleStatusChanged(request.data());
            case "DEPOSIT_CALLBACK" -> handleDepositCallback(secret, request.data());
            default -> log.warn("처리할 수 없는 웹훅 이벤트: {}", eventType);
        }
    }

    private void handleStatusChanged(TossPaymentData data) {
        Payment foundPayment = paymentJpaRepository.findByOrderNumber(data.orderId())
                .orElseThrow(() -> new ApplicationException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        // 멱등성 처리: 상태값 확인 후 상태전이가 이미 진행되었다면 수신된 웹훅은 무시한다.
        switch (data.status()) {
            case "DONE" -> {
                if (foundPayment.getStatus() == PaymentStatus.DONE) {
                    log.info("이미 처리된 DONE 웹훅: {}", data.orderId());
                    return;
                }
                // 망취소 대응: 우리는 FAILED지만 실제로는 결제 완료된 케이스
                foundPayment.approve(data.paymentKey(), PaymentMethod.from(data.method()));
                Order foundOrder = orderQueryService.findByOrderNumber(data.orderId());
                foundOrder.markPaid();
            }
            case "ABORTED" -> {
                if (foundPayment.getStatus() == PaymentStatus.FAILED) {
                    log.info("이미 처리된 ABORTED 웹훅: {}", data.orderId());
                    return;
                }
                foundPayment.reject();
                Order foundOrder = orderQueryService.findByOrderNumber(data.orderId());
                foundOrder.markPaymentFailed();
            }
            case "EXPIRED" -> {
                if (foundPayment.getStatus() == PaymentStatus.EXPIRED) {
                    log.info("이미 처리된 EXPIRED 웹훅: {}", data.orderId());
                    return;
                }
                foundPayment.expire();
            }
            case "CANCELED" -> {
                if (foundPayment.getStatus() == PaymentStatus.CANCELED) {
                    log.info("이미 처리된 CANCEL 웹훅: {}", data.orderId());
                    return;
                }
                foundPayment.cancelFully();
            }
            case "PARTIAL_CANCELED" -> {
                if (foundPayment.getStatus() == PaymentStatus.PARTIAL_CANCELED) {
                    log.info("이미 처리된 PARTIAL_CANCELED 웹훅: {}", data.orderId());
                    return;
                }
                // 웹훅 데이터만으로는 어떤 OrderItem이 취소됐는지 알 수 없기에 처리할 수 없음
                log.warn("미처리 PARTIAL_CANCELED 웹훅 수신: {}", data.orderId());
            }
            case "WAITING_FOR_DEPOSIT" -> {
                if (foundPayment.getStatus() == PaymentStatus.WAITING_FOR_DEPOSIT) {
                    log.info("이미 처리된 WAITING_FOR_DEPOSIT 웹훅: {}", data.orderId());
                    return;
                }
                log.warn("미처리 WAITING_FOR_DEPOSIT 웹훅 수신: {}", data.orderId());
            }
            default -> log.warn("처리할 수 없는 웹훅 수신: 상태 = {}, 주문번호 = {}", data.status(), data.orderId());
        }
    }

    // 가상결제 완료
    private void handleDepositCallback(String secret, TossPaymentData data) {
        Payment foundPayment = paymentJpaRepository.findByOrderNumber(data.orderId())
                .orElseThrow(() -> new ApplicationException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        // secret 검증
        if (!foundPayment.getWebhookSecret().equals(secret)) {
            throw new ApplicationException(PaymentErrorCode.INVALID_WEBHOOK_SECRET);
        }

        foundPayment.completeDeposit();
        Order foundOrder = orderQueryService.findByOrderNumber(data.orderId());
        foundOrder.markPaid();
    }



}
