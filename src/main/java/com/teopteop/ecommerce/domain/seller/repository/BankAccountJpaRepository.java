package com.teopteop.ecommerce.domain.seller.repository;

import com.teopteop.ecommerce.domain.seller.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountJpaRepository extends JpaRepository<BankAccount, Long> {
}
