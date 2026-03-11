package com.teopteop.ecommerce.domain.account.repository;

import com.teopteop.ecommerce.domain.account.entity.Account;
import com.teopteop.ecommerce.domain.account.entity.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountJpaRepository extends JpaRepository<Account, Long> {

    boolean existsByUsername(String username);

    @Query("select a from Account a where a.username = :username and a.status = 'ACTIVE'")
    Optional<Account> findActiveAccountByUsername(@Param("username") String username);

    @Query("select a from Account a where a.id = :id and a.status = 'ACTIVE'")
    Optional<Account> findActiveAccountById(@Param("id") Long id);

}
