package com.teopteop.ecommerce.domain.auth.application;

import com.teopteop.ecommerce.domain.auth.domain.User;
import com.teopteop.ecommerce.domain.auth.domain.UserRepository;
import com.teopteop.ecommerce.domain.auth.domain.UserRole;
import com.teopteop.ecommerce.domain.auth.dto.LoginRequest;
import com.teopteop.ecommerce.domain.auth.dto.LoginResponse;
import com.teopteop.ecommerce.domain.auth.dto.SignUpRequest;
import com.teopteop.ecommerce.domain.auth.dto.SignUpResponse;
import com.teopteop.ecommerce.domain.auth.exception.AuthErrorCode;
import com.teopteop.ecommerce.domain.auth.exception.AuthException;
import com.teopteop.ecommerce.domain.auth.exception.UserErrorCode;
import com.teopteop.ecommerce.domain.auth.exception.UserException;
import com.teopteop.ecommerce.domain.member.domain.Address;
import com.teopteop.ecommerce.domain.member.domain.Member;
import com.teopteop.ecommerce.domain.member.exception.MemberErrorCode;
import com.teopteop.ecommerce.domain.member.exception.MemberException;
import com.teopteop.ecommerce.domain.member.infra.MemberJpaRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final UserRepository userRepository;
    private final MemberJpaRepository memberRepository; //현재 단계에서 Member도메인 저장소는 추상화하지 않음
    private final PasswordEncoder passwordEncoder;

    public SignUpResponse registerUser(SignUpRequest request) {
        if(memberRepository.existsByEmail(request.email())) {
            throw new MemberException(MemberErrorCode.EMAIL_DUPLICATE);
        }

        if(userRepository.existsByUsername(request.username())) {
            throw new UserException(UserErrorCode.USERNAME_DUPLICATE);
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

        Member savedMember = memberRepository.save(member);

        User user = User.create(
                request.username(),
                passwordEncoder.encode(request.password()),
                UserRole.ROLE_USER,
                savedMember
        );

        User savedUser = userRepository.save(user);

        return new SignUpResponse(savedUser.getId(), savedUser.getUsername());
    }


    public LoginResponse authenticate(LoginRequest request) {
        User findUser = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_CREDENTIALS));

        if(!passwordEncoder.matches(request.password(), findUser.getPassword())) {
            throw new AuthException(AuthErrorCode.INVALID_CREDENTIALS);
        }

        String accessToken = "";

        return new LoginResponse(accessToken);
    }
}
