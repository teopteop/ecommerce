package com.teopteop.ecommerce.domain.auth.domain;

import java.util.Optional;

public interface UserRepository {

    User save(User user);

    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);
}
