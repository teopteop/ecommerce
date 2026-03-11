package com.teopteop.ecommerce.domain.auth.service;

import com.teopteop.ecommerce.domain.auth.dto.LoginRequest;
import com.teopteop.ecommerce.domain.auth.dto.LoginResponse;
import com.teopteop.ecommerce.domain.auth.dto.SignUpRequest;
import com.teopteop.ecommerce.domain.auth.dto.SignUpResponse;
import com.teopteop.ecommerce.domain.auth.entity.Account;
import com.teopteop.ecommerce.domain.auth.entity.AccountRole;
import com.teopteop.ecommerce.domain.auth.exception.AuthErrorCode;
import com.teopteop.ecommerce.domain.auth.exception.AuthException;
import com.teopteop.ecommerce.domain.auth.repository.AccountJpaRepository;
import com.teopteop.ecommerce.domain.member.entity.Member;
import com.teopteop.ecommerce.domain.member.service.MemberCommandService;
import com.teopteop.ecommerce.domain.member.service.MemberQueryService;
import com.teopteop.ecommerce.global.common.vo.Address;
import com.teopteop.ecommerce.global.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountCommandService {

    private final AccountJpaRepository accountJpaRepository;

    private final MemberCommandService memberCommandService;

    private final MemberQueryService memberQueryService;

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public SignUpResponse registerAccount(SignUpRequest request) {
        memberQueryService.validateEmailNotDuplicate(request.email());

        if(accountJpaRepository.existsByUsername(request.username())) {
            throw new AuthException(AuthErrorCode.USERNAME_DUPLICATE);
        }

        Member member = Member.create(
                request.name(),
                request.email(),
                request.phoneNumber(),
                new Address(
                        request.address().city(),
                        request.address().street(),
                        request.address().zipcode()
                )
        );

        Member savedMember = memberCommandService.registerMember(member);

        Account account = Account.create(
                request.username(),
                passwordEncoder.encode(request.password()),
                AccountRole.ROLE_USER,
                savedMember.getId()
        );

        Account savedAccount = accountJpaRepository.save(account);

        return new SignUpResponse(savedAccount.getId(), savedAccount.getUsername());
    }

    // 조회용 메서드지만 '인증 행위' 자체가 Command에 가깝고 JWT 발급이 포함되므로 AuthQueryService로 분리하지 않음
    @Transactional(readOnly = true)
    public LoginResponse authenticate(LoginRequest request) {
        Account findAccount = accountJpaRepository.findByUsername(request.username())
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_CREDENTIALS));

        if(!passwordEncoder.matches(request.password(), findAccount.getPassword())) {
            throw new AuthException(AuthErrorCode.INVALID_CREDENTIALS);
        }

        String accessToken = jwtUtil.createAccessToken(findAccount.getId(), findAccount.getMemberId(), findAccount.getRole());

        return new LoginResponse(accessToken);
    }
}
