package com.teopteop.ecommerce.global.security.jwt;

import com.teopteop.ecommerce.domain.account.entity.AccountRole;
import com.teopteop.ecommerce.domain.account.repository.AccountJpaRepository;
import com.teopteop.ecommerce.global.security.principal.AccountPrincipal;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long accessTokenValidity;
    private final long refreshTokenValidity;

    /**
     * JWT Util 생성자
     * @param secret Base64로 인코딩된 비밀 키
     * @param accessTokenValidity 액세스 토큰 유효시간(ms)
     * @param refreshTokenValidity 리프레시 토큰 유효시간(ms)
     * @param accountJpaRepository 계정 존재 여부 확인용 Repository
     */
    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration}") long accessTokenValidity,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenValidity,
            AccountJpaRepository accountJpaRepository
    ) {
        byte[] decodedKey = Base64.getDecoder().decode(secret);
        if(decodedKey.length < 32) {
            log.error("JWT Secret Key 설정 오류: 환경변수 값이 잘못되었거나 길이가 짧습니다.");
            throw new JwtException(JwtErrorCode.SECRET_KEY_INVALID);
        }
        this.secretKey = Keys.hmacShaKeyFor(decodedKey);
        this.accessTokenValidity = accessTokenValidity;
        this.refreshTokenValidity = refreshTokenValidity;
    }

    /**
     * 액세스 토큰 생성
     * @param accountId 계정 Id
     * @param accountRole 계정 권한(Role)
     * @return 서명된 JWT 액세스 토큰
     */
    public String createAccessToken(Long accountId, Long customerId, AccountRole accountRole) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenValidity);

        return Jwts.builder()
                .setSubject(String.valueOf(accountId))            // JWT Subject = accountId
                .claim("role", accountRole.name())              // Custom Claim에 Role 추가
                .claim("customerId", customerId)                 // Custom Claim에 customerId 추가
                .setIssuedAt(now)                              // 발급시간
                .setExpiration(expiryDate)                     // 만료시간
                .signWith(secretKey, SignatureAlgorithm.HS256) // HS256 서명
                .compact();
    }

    /**
     * 액세스 토큰 생성
     * @param accountId 계정 Id
     * @return 서명된 JWT 리프레시 토큰
     */
    public String createRefreshToken(Long accountId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenValidity);

        return Jwts.builder()
                .setSubject(String.valueOf(accountId))
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 토큰 유효성 검증
     * @param token JWT 토큰
     * @return
     *  - null : 토큰이 유효함
     *  - JwtErrorCode : 토큰 검증 실패 사유
     */
    public JwtErrorCode validateToken(String token) {
        try {
            // 서명, 만료, 구조 검증 진행
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return null;
        } catch (ExpiredJwtException e) {
            return JwtErrorCode.TOKEN_EXPIRED;
        } catch (UnsupportedJwtException e) {
            return JwtErrorCode.TOKEN_UNSUPPORTED;
        } catch (MalformedJwtException | IllegalArgumentException e) {
            return JwtErrorCode.TOKEN_MALFORMED;
        } catch (SecurityException e) {
            return JwtErrorCode.TOKEN_SIGNATURE_INVALID;
        }
    }

    /**
     * 토큰에서 Authentication 객체 생성
     * @param token JWT 토큰
     * @return Spring Security Authentication
     */
    public Authentication getAuthentication(String token) {
        Long accountId = getAccountId(token);
        Long customerId = getCustomerId(token);
        AccountRole role = getAccountRole(token);

        AccountPrincipal principal = new AccountPrincipal(accountId, customerId, String.valueOf(accountId), "", role);
        return new UsernamePasswordAuthenticationToken(principal, token, principal.getAuthorities());
    }

    public long getAccountId(String token) {
        return Long.parseLong(parseClaims(token).getSubject());
    }

    public long getCustomerId(String token) {
        return parseClaims(token).get("customerId", Long.class);
    }

    public AccountRole getAccountRole(String token) {
        String role = parseClaims(token).get("role", String.class);

        return role != null ? AccountRole.valueOf(role) : null;
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
