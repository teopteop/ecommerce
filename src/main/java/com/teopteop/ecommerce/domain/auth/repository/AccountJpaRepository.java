package com.teopteop.ecommerce.domain.auth.repository;

import com.teopteop.ecommerce.domain.auth.entity.Account;
import com.teopteop.ecommerce.domain.auth.entity.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountJpaRepository extends JpaRepository<Account, Long> {

    boolean existsByUsername(String username);

    Optional<Account> findByUsername(String username);

    @Query("select a from Account a where a.id = :id and a.status = 'ACTIVE'")
    Optional<Account> findActiveAccountById(@Param("id") Long id);

    boolean existsActiveAccountById(Long accountId, AccountStatus status);

}
