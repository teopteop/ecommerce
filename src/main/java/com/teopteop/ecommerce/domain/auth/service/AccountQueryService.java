package com.teopteop.ecommerce.domain.auth.service;

import com.teopteop.ecommerce.domain.auth.entity.Account;
import com.teopteop.ecommerce.domain.auth.exception.AuthErrorCode;
import com.teopteop.ecommerce.domain.auth.exception.AuthException;
import com.teopteop.ecommerce.domain.auth.repository.AccountJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountQueryService {

    private final AccountJpaRepository accountJpaRepository;

    public Account findActiveAccountById(Long accountId) {
        return accountJpaRepository.findActiveAccountById(accountId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));
    }
}
