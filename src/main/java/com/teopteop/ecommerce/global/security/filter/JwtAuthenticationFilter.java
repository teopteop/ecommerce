package com.teopteop.ecommerce.global.security.filter;

import com.teopteop.ecommerce.global.security.jwt.JwtErrorCode;
import com.teopteop.ecommerce.global.security.jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    /**
     * 매 요청마다 실행되는 JWT 인증 필터
     * 1. Authorization 헤더에서 JWT 추출
     * 2. 토큰 유효성 검증
     *  - 유효한 경우: Authentication 생성후 SecurityContext에 저장
     *  - 유효하지 않은 경우: 에러코드를 request attribute에 저장
     * 3. 다음 필터로 요청 전달
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String token = resolveToken(request);

        if (token != null) {
            JwtErrorCode errorCode = jwtUtil.validateToken(token);

            if (errorCode == null) {
                //토큰이 유효한 경우 -> Authentication 생성
                Authentication authentication = jwtUtil.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                // 토큰이 유효하지 않은 경우 -> EntryPoint에서 사용할 에러코드 전송
                request.setAttribute("jwtError", errorCode);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken =  request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
