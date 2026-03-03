package com.teopteop.ecommerce.domain.payment.client;

import com.teopteop.ecommerce.domain.payment.dto.TossConfirmRequest;
import com.teopteop.ecommerce.domain.payment.dto.TossConfirmResponse;
import com.teopteop.ecommerce.domain.payment.exception.PaymentErrorCode;
import com.teopteop.ecommerce.global.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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
}
