package com.teopteop.ecommerce.domain.account.repository;

import com.teopteop.ecommerce.domain.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountJpaRepository extends JpaRepository<Account, Long> {

    boolean existsByEmail(String email);

    @Query("select a from Account a where a.email = :email and a.status = 'ACTIVE'")
    Optional<Account> findActiveAccountByEmail(@Param("email") String email);

    @Query("select a from Account a where a.id = :id and a.status = 'ACTIVE'")
    Optional<Account> findActiveAccountById(@Param("id") Long id);

}
