package com.teopteop.ecommerce.domain.auth.domain;

import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository {

    User save(User user);

    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);

    Optional<User> findActiveUserById(Long userId);

    boolean existsActiveUserById(Long userId, UserStatus status);
}
