package com.teopteop.ecommerce.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;

@Configuration
public class WebClientConfig {

    @Value("${toss.secret-key}")
    private String secretKey; // toss 테스트 키

    @Bean
    public WebClient tossWebClient() {
        /*
         * Base64 인코딩을 사용하는 이유
         * BCryptPasswordEncoder는 단방향 해시라 복호화가 불가
         * 토스 서버는 Authorization 헤더를 Base64로 디코딩해서 시크릿 키를 검증
         * 양방향 변환이 가능한 Base64 인코딩을 사용해야 한다.
         */
        String encodedKey = Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes());

        return WebClient.builder()
                .baseUrl("https://api.tosspayments.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + encodedKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
