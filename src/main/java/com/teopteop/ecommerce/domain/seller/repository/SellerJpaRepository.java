package com.teopteop.ecommerce.domain.seller.repository;

import com.teopteop.ecommerce.domain.seller.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerJpaRepository extends JpaRepository<Seller, Long> {
}
