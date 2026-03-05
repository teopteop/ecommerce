package com.teopteop.ecommerce.domain.auth.service;

import com.teopteop.ecommerce.domain.auth.entity.User;
import com.teopteop.ecommerce.domain.auth.exception.UserErrorCode;
import com.teopteop.ecommerce.domain.auth.repository.UserJpaRepository;
import com.teopteop.ecommerce.global.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserQueryService {

    private final UserJpaRepository userJpaRepository;

    public User findActiveUserById(Long userId) {
        return userJpaRepository.findActiveUserById(userId)
                .orElseThrow(() -> new ApplicationException(UserErrorCode.USER_NOT_FOUND));
    }
}
