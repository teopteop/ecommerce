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
public class AuthService {

    private final MemberJpaRepository memberJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public SignUpResponse registerUser(SignUpRequest request) {
        if(memberJpaRepository.existsByEmail(request.email())) {
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

        Member savedMember = memberJpaRepository.save(member);

        User user = User.create(
                request.username(),
                passwordEncoder.encode(request.password()),
                UserRole.ROLE_USER,
                savedMember
        );

        User savedUser = userJpaRepository.save(user);

        return new SignUpResponse(savedUser.getId(), savedUser.getUsername());
    }

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
