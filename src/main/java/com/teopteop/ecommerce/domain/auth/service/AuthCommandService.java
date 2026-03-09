package com.teopteop.ecommerce.domain.auth.service;

import com.teopteop.ecommerce.domain.auth.entity.User;
import com.teopteop.ecommerce.domain.auth.entity.UserRole;
import com.teopteop.ecommerce.domain.auth.dto.LoginRequest;
import com.teopteop.ecommerce.domain.auth.dto.LoginResponse;
import com.teopteop.ecommerce.domain.auth.dto.SignUpRequest;
import com.teopteop.ecommerce.domain.auth.dto.SignUpResponse;
import com.teopteop.ecommerce.domain.auth.exception.AuthErrorCode;
import com.teopteop.ecommerce.domain.auth.exception.UserErrorCode;
import com.teopteop.ecommerce.domain.auth.repository.UserJpaRepository;
import com.teopteop.ecommerce.domain.member.service.MemberCommandService;
import com.teopteop.ecommerce.domain.member.service.MemberQueryService;
import com.teopteop.ecommerce.global.common.vo.Address;
import com.teopteop.ecommerce.domain.member.entity.Member;
import com.teopteop.ecommerce.domain.member.exception.MemberErrorCode;
import com.teopteop.ecommerce.domain.member.repository.MemberJpaRepository;
import com.teopteop.ecommerce.global.exception.ApplicationException;
import com.teopteop.ecommerce.global.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthCommandService {

    private final UserJpaRepository userJpaRepository;

    private final MemberCommandService memberCommandService;

    private final MemberQueryService memberQueryService;

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public SignUpResponse registerUser(SignUpRequest request) {
        if(memberQueryService.existsByEmail(request.email())) {
            throw new ApplicationException(MemberErrorCode.EMAIL_DUPLICATE);
        }

        if(userJpaRepository.existsByUsername(request.username())) {
            throw new ApplicationException(UserErrorCode.USERNAME_DUPLICATE);
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

        User user = User.create(
                request.username(),
                passwordEncoder.encode(request.password()),
                UserRole.ROLE_USER,
                savedMember.getId()
        );

        User savedUser = userJpaRepository.save(user);

        return new SignUpResponse(savedUser.getId(), savedUser.getUsername());
    }

    // 조회용 메서드지만 '인증 행위' 자체가 Command에 가깝고 JWT 발급이 포함되므로 AuthQueryService로 분리하지 않음
    @Transactional(readOnly = true)
    public LoginResponse authenticate(LoginRequest request) {
        User findUser = userJpaRepository.findByUsername(request.username())
                .orElseThrow(() -> new ApplicationException(AuthErrorCode.INVALID_CREDENTIALS));

        if(!passwordEncoder.matches(request.password(), findUser.getPassword())) {
            throw new ApplicationException(AuthErrorCode.INVALID_CREDENTIALS);
        }

        String accessToken = jwtUtil.createAccessToken(findUser.getId(), findUser.getRole());

        return new LoginResponse(accessToken);
    }
}
