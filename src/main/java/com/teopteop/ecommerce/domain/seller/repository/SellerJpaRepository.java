package com.teopteop.ecommerce.domain.seller.repository;

import com.teopteop.ecommerce.domain.seller.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SellerJpaRepository extends JpaRepository<Seller, Long> {

    Optional<Seller> findByAccountId(Long accountId);
}
