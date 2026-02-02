package com.teopteop.ecommerce.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            // SecurityContext에서 인증 객체 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (
                    authentication == null // SecurityContext에 인증정보가 없을 때
                    || !authentication.isAuthenticated()  // 인증 객체는 있지만 인증되지 않은 상태
                    || authentication instanceof AnonymousAuthenticationToken // 익명토큰일 때
            ) {
                return Optional.empty();
            }

            UserDetails principal = (UserDetails) authentication.getPrincipal();
            return Optional.of(principal.getUsername());
        };
    }
}
