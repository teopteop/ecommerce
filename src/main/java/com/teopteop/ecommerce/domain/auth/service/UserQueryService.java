package com.teopteop.ecommerce.domain.auth.service;

import com.teopteop.ecommerce.domain.auth.entity.User;
import com.teopteop.ecommerce.domain.auth.exception.AuthErrorCode;
import com.teopteop.ecommerce.domain.auth.exception.AuthException;
import com.teopteop.ecommerce.domain.auth.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserQueryService {

    private final UserJpaRepository userJpaRepository;

    public User findActiveUserById(Long userId) {
        return userJpaRepository.findActiveUserById(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));
    }
}
