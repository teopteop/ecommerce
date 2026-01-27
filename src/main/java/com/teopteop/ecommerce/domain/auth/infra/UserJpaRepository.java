package com.teopteop.ecommerce.domain.auth.infra;

import com.teopteop.ecommerce.domain.auth.domain.User;
import com.teopteop.ecommerce.domain.auth.domain.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);

    @Query("select u from User u where u.id = :userId and u.status = 'ACTIVE'")
    Optional<User> findActiveUserById(@Param("userId") Long userId);

    boolean existsActiveUserById(Long userId, UserStatus status);
}
