package com.teopteop.ecommerce.domain.auth.infra;

import com.teopteop.ecommerce.domain.auth.domain.User;
import com.teopteop.ecommerce.domain.auth.domain.UserRepository;
import com.teopteop.ecommerce.domain.auth.domain.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository jpaRepository;

    @Override
    public boolean existsByUsername(String username) {
        return jpaRepository.existsByUsername(username);
    }

    @Override
    public User save(User user) {
        return jpaRepository.save(user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jpaRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findActiveUserById(Long userId) {
        return jpaRepository.findActiveUserById(userId);
    }

    @Override
    public boolean existsActiveUserById(Long userId, UserStatus status) {
        return jpaRepository.existsActiveUserById(userId, status);
    }
}
