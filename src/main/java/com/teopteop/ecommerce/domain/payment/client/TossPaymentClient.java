package com.teopteop.ecommerce.domain.payment.client;

import com.teopteop.ecommerce.domain.payment.dto.TossCancelRequest;
import com.teopteop.ecommerce.domain.payment.dto.TossConfirmRequest;
import com.teopteop.ecommerce.domain.payment.dto.TossConfirmResponse;
import com.teopteop.ecommerce.domain.payment.dto.TossPartialCancelRequest;
import com.teopteop.ecommerce.domain.payment.exception.PaymentErrorCode;
import com.teopteop.ecommerce.global.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class TossPaymentClient {

    private final WebClient tossWebClient;

    public TossConfirmResponse confirm(TossConfirmRequest request) {
        return tossWebClient.post()
                .uri("/v1/payments/confirm")
                .bodyValue(request)
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        clientResponse -> Mono.error(
                                new ApplicationException(PaymentErrorCode.PAYMENT_CONFIRM_FAILED)
                        )
                ) // 에러 응답 -> 프로젝트 예외로 반환
                .bodyToMono(TossConfirmResponse.class) // 정상 응답 DTO로 반환
                .block();
    }

    public void cancelFully(String paymentKey, String cancelReason) {
        tossWebClient.post()
                .uri("/v1/payments/" + paymentKey + "/cancel")
                .bodyValue(new TossCancelRequest(cancelReason))
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        clientResponse -> Mono.error(
                                new ApplicationException(PaymentErrorCode.PAYMENT_CANCEL_FAILED)
                        )
                )
                .bodyToMono(Void.class)
                .block();
    }

    public void cancelPartially(String paymentKey, String cancelReason, BigDecimal cancelAmount) {
        tossWebClient.post()
                .uri("/v1/payments/" + paymentKey + "/cancel")
                .bodyValue(new TossPartialCancelRequest(cancelReason, cancelAmount))
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        clientResponse -> Mono.error(
                                new ApplicationException(PaymentErrorCode.PAYMENT_CANCEL_FAILED)
                        )
                )
                .bodyToMono(Void.class)
                .block();
    }
}
