package com.teopteop.ecommerce.domain.inventory.repository;

import com.teopteop.ecommerce.domain.inventory.entity.Inventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InventoryJpaRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByProductId(Long productId);

    @Query("select i from Inventory i where i.quantity < :threshold")
    Page<Inventory> findInventoriesUnderThreshold(@Param("threshold") int threshold, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Inventory i where i.productId in :productIds")
    List<Inventory> findByProductIdsWithLock(@Param("productIds") List<Long> productIds);
}
