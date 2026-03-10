package com.teopteop.ecommerce.domain.inventory.service;

import com.teopteop.ecommerce.domain.inventory.dto.InventoryResponse;
import com.teopteop.ecommerce.domain.inventory.entity.Inventory;
import com.teopteop.ecommerce.domain.inventory.exception.InventoryErrorCode;
import com.teopteop.ecommerce.domain.inventory.exception.InventoryException;
import com.teopteop.ecommerce.domain.inventory.repository.InventoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventoryQueryService {

    private final InventoryJpaRepository inventoryJpaRepository;

    public InventoryResponse findInventory(Long id) {
        return InventoryResponse.fromEntity(inventoryJpaRepository.findById(id)
                .orElseThrow(() -> new InventoryException(InventoryErrorCode.INVENTORY_NOT_FOUND)));
    }

    public InventoryResponse findByProductId(Long productId) {
        return InventoryResponse.fromEntity(inventoryJpaRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryException(InventoryErrorCode.INVENTORY_NOT_FOUND)));
    }

    public Page<InventoryResponse> findInventoriesUnderThreshold(int threshold, Pageable pageable) {
        Page<Inventory> foundInventories = inventoryJpaRepository.findInventoriesUnderThreshold(threshold, pageable);
        return foundInventories.map(InventoryResponse::fromEntity);
    }

    public Page<InventoryResponse> findInventories(Pageable pageable) {
        Page<Inventory> foundInventories = inventoryJpaRepository.findAll(pageable);

        return foundInventories.map(InventoryResponse::fromEntity);
    }
}
