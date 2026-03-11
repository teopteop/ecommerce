package com.teopteop.ecommerce.domain.auth.service;

import com.teopteop.ecommerce.domain.account.entity.Account;
import com.teopteop.ecommerce.domain.account.entity.AccountRole;
import com.teopteop.ecommerce.domain.account.service.AccountCommandService;
import com.teopteop.ecommerce.domain.account.service.AccountQueryService;
import com.teopteop.ecommerce.domain.auth.dto.LoginRequest;
import com.teopteop.ecommerce.domain.auth.dto.LoginResponse;
import com.teopteop.ecommerce.domain.auth.dto.SignUpRequest;
import com.teopteop.ecommerce.domain.auth.dto.SignUpResponse;
import com.teopteop.ecommerce.domain.auth.exception.AuthErrorCode;
import com.teopteop.ecommerce.domain.auth.exception.AuthException;
import com.teopteop.ecommerce.domain.customer.entity.Customer;
import com.teopteop.ecommerce.domain.customer.service.CustomerCommandService;
import com.teopteop.ecommerce.domain.customer.service.CustomerQueryService;
import com.teopteop.ecommerce.global.common.vo.Address;
import com.teopteop.ecommerce.global.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthCommandService {

    private final AccountCommandService accountCommandService;
    private final CustomerCommandService customerCommandService;

    private final AccountQueryService accountQueryService;
    private final CustomerQueryService customerQueryService;

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public SignUpResponse registerCustomerAccount(SignUpRequest request) {
        // 1. Email 중복 검증
        customerQueryService.validateEmailNotDuplicate(request.email());

        // 2. Username 중복 검증
        accountQueryService.validateUsernameNotDuplicate(request.username());

        // 3. 고객 생성 및 저장
        Customer customer = Customer.create(
                request.name(),
                request.email(),
                request.phoneNumber(),
                new Address(
                        request.address().city(),
                        request.address().street(),
                        request.address().zipcode()
                )
        );

        Customer savedCustomer = customerCommandService.registerCustomer(customer);

        // 4. 계정 생성 및 저장
        Account account = Account.create(
                request.username(),
                passwordEncoder.encode(request.password()),
                AccountRole.ROLE_CUSTOMER,
                savedCustomer.getId()
        );

        Account savedAccount = accountCommandService.registerAccount(account);

        return new SignUpResponse(savedAccount.getId(), savedAccount.getUsername());
    }

    // 조회용 메서드지만 '인증 행위' 자체가 Command에 가깝고 JWT 발급이 포함되므로 AuthQueryService로 분리하지 않음
    @Transactional(readOnly = true)
    public LoginResponse authenticate(LoginRequest request) {
        Account findAccount = accountQueryService.findActiveAccountByUsername(request.username());

        if(!passwordEncoder.matches(request.password(), findAccount.getPassword())) {
            throw new AuthException(AuthErrorCode.INVALID_CREDENTIALS);
        }

        String accessToken = jwtUtil.createAccessToken(findAccount.getId(), findAccount.getCustomerId(), findAccount.getRole());

        return new LoginResponse(accessToken);
    }
}
