package com.teopteop.ecommerce.domain.account.service;

import com.teopteop.ecommerce.domain.account.entity.Account;
import com.teopteop.ecommerce.domain.account.repository.AccountJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountCommandService {

    private final AccountJpaRepository accountJpaRepository;

    public Account registerAccount(Account account) {
        return accountJpaRepository.save(account);
    }
}
