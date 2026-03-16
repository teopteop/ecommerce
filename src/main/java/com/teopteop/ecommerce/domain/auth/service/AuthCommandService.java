package com.teopteop.ecommerce.domain.auth.service;

import com.teopteop.ecommerce.domain.account.entity.Account;
import com.teopteop.ecommerce.domain.account.entity.AccountRole;
import com.teopteop.ecommerce.domain.account.service.AccountCommandService;
import com.teopteop.ecommerce.domain.account.service.AccountQueryService;
import com.teopteop.ecommerce.domain.auth.dto.*;
import com.teopteop.ecommerce.domain.auth.exception.AuthErrorCode;
import com.teopteop.ecommerce.domain.auth.exception.AuthException;
import com.teopteop.ecommerce.domain.customer.entity.Customer;
import com.teopteop.ecommerce.domain.customer.service.CustomerCommandService;
import com.teopteop.ecommerce.domain.seller.entity.BankAccount;
import com.teopteop.ecommerce.domain.seller.entity.Seller;
import com.teopteop.ecommerce.domain.seller.service.SellerCommandService;
import com.teopteop.ecommerce.domain.seller.vo.BusinessInfo;
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
    private final SellerCommandService sellerCommandService;

    private final AccountQueryService accountQueryService;

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public SignUpCustomerResponse registerCustomerAccount(SignUpCustomerRequest request) {
        // 1. Email 중복 검증
        accountQueryService.validateEmailNotDuplicate(request.email());

        // 2. 계정 생성 및 저장
        Account savedAccount = accountCommandService.registerAccount(
                request.email(),
                passwordEncoder.encode(request.password()),
                AccountRole.ROLE_CUSTOMER);

        // 3. 고객 생성 및 저장
        Customer savedCustomer = customerCommandService.registerCustomer(
                savedAccount.getId(),
                request.name(),
                request.phoneNumber(),
                new Address(
                        request.address().city(),
                        request.address().street(),
                        request.address().zipcode()
                ));

        return new SignUpCustomerResponse(
                savedCustomer.getId(),
                savedCustomer.getAccountId(),
                savedAccount.getEmail()
        );
    }

    public SignUpSellerResponse registerSellerAccount(SignUpSellerRequest request) {

        // 1. Email 중복 검증
        accountQueryService.validateEmailNotDuplicate(request.email());

        // 2. 계정 생성 및 저장
        Account savedAccount = accountCommandService.registerAccount(
                request.email(),
                passwordEncoder.encode(request.password()),
                AccountRole.ROLE_SELLER
        );

        // 3. Seller 생성 및 저장
        Seller savedSeller = sellerCommandService.registerSeller(
                savedAccount.getId(),
                new BusinessInfo(
                        request.businessInfo().businessName(),
                        request.businessInfo().businessNumber(),
                        request.businessInfo().representativeName()
                ),
                request.phoneNumber(),
                new Address(
                        request.address().city(),
                        request.address().street(),
                        request.address().zipcode()
                )
        );

        // 4. BankAccount 생성 및 저장
        sellerCommandService.registerBankAccount(
                savedSeller.getId(),
                request.bankAccount().bankCode(),
                request.bankAccount().accountNumber(),
                request.bankAccount().holderName()
        );

        return new SignUpSellerResponse(
                savedSeller.getId(),
                savedAccount.getId(),
                savedAccount.getEmail()
        );
    }

    // 조회용 메서드지만 '인증 행위' 자체가 Command에 가깝고 JWT 발급이 포함되므로 AuthQueryService로 분리하지 않음
    @Transactional(readOnly = true)
    public LoginResponse authenticate(LoginRequest request) {
        Account findAccount = accountQueryService.findActiveAccountByEmail(request.email());

        if(!passwordEncoder.matches(request.password(), findAccount.getPassword())) {
            throw new AuthException(AuthErrorCode.INVALID_CREDENTIALS);
        }

        String accessToken = jwtUtil.createAccessToken(findAccount.getId(), findAccount.getRole());

        return new LoginResponse(accessToken);
    }
}
