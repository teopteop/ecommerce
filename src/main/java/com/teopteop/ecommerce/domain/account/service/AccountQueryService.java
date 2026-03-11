package com.teopteop.ecommerce.domain.account.service;

import com.teopteop.ecommerce.domain.account.entity.Account;
import com.teopteop.ecommerce.domain.account.exception.AccountErrorCode;
import com.teopteop.ecommerce.domain.account.exception.AccountException;
import com.teopteop.ecommerce.domain.account.repository.AccountJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountQueryService {

    private final AccountJpaRepository accountJpaRepository;

    public Account findActiveAccountById(Long accountId) {
        return accountJpaRepository.findActiveAccountById(accountId)
                .orElseThrow(() -> new AccountException(AccountErrorCode.USER_NOT_FOUND));
    }

    public void validateUsernameNotDuplicate(String username) {
        if (accountJpaRepository.existsByUsername(username)) {
            throw new AccountException(AccountErrorCode.USERNAME_DUPLICATE);
        }
    }

    public Account findActiveAccountByUsername(String username) {
        return accountJpaRepository.findActiveAccountByUsername(username)
                .orElseThrow(() -> new AccountException(AccountErrorCode.INVALID_CREDENTIALS));
    }
}
