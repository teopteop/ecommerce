package com.teopteop.ecommerce.domain.inventory.repository;

import com.teopteop.ecommerce.domain.inventory.entity.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InventoryJpaRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByProductId(Long productId);

    @Query("select i from Inventory i where i.quantity < :threshold")
    Page<Inventory> findInventoriesUnderThreshold(@Param("threshold") int threshold, Pageable pageable);
}
