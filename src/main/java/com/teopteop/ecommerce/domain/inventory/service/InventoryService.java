package com.teopteop.ecommerce.domain.inventory.service;

import com.teopteop.ecommerce.domain.inventory.dto.InventoryResponse;
import com.teopteop.ecommerce.domain.inventory.dto.InventoryStockRequest;
import com.teopteop.ecommerce.domain.inventory.entity.Inventory;
import com.teopteop.ecommerce.domain.inventory.exception.InventoryErrorCode;
import com.teopteop.ecommerce.domain.inventory.repository.InventoryJpaRepository;
import com.teopteop.ecommerce.global.common.dto.ApiResponse;
import com.teopteop.ecommerce.global.common.dto.PageResponse;
import com.teopteop.ecommerce.global.exception.ApplicationException;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InventoryService {

    private final InventoryJpaRepository inventoryJpaRepository;

    @Transactional(readOnly = true)
    public InventoryResponse findInventory(Long id) {
        return InventoryResponse.fromEntity(inventoryJpaRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(InventoryErrorCode.INVENTORY_NOT_FOUND)));
    }

    @Transactional(readOnly = true)
    public InventoryResponse findByProductId(Long productId) {
        return InventoryResponse.fromEntity(inventoryJpaRepository.findByProductId(productId)
                .orElseThrow(() -> new ApplicationException(InventoryErrorCode.INVENTORY_NOT_FOUND)));
    }

    @Transactional(readOnly = true)
    public Page<InventoryResponse> findInventoriesUnderThreshold(int threshold, Pageable pageable) {
        Page<Inventory> foundInventories = inventoryJpaRepository.findInventoriesUnderThreshold(threshold, pageable);
        return foundInventories.map(InventoryResponse::fromEntity);
    }

    public void restock(Long id, InventoryStockRequest request) {
        Inventory foundInventory = inventoryJpaRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(InventoryErrorCode.INVENTORY_NOT_FOUND));

        foundInventory.increase(request.amount());
        log.info("재고가 입고되었습니다. 수량:{}, 사유:{}", request.amount(), request.reason());
    }

    public void deduct(Long id, InventoryStockRequest request) {
        Inventory foundInventory = inventoryJpaRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(InventoryErrorCode.INVENTORY_NOT_FOUND));

        foundInventory.decrease(request.amount());
        log.info("재고가 차감되었습니다. 수량:{}, 사유:{}", request.amount(), request.reason());
    }

    public Page<InventoryResponse> findInventories(Pageable pageable) {
        Page<Inventory> foundInventories = inventoryJpaRepository.findAll(pageable);

        return foundInventories.map(InventoryResponse::fromEntity);
    }
}
