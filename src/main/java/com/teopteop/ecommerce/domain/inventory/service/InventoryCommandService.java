package com.teopteop.ecommerce.domain.inventory.service;

import com.teopteop.ecommerce.domain.inventory.dto.InventoryStockRequest;
import com.teopteop.ecommerce.domain.inventory.entity.Inventory;
import com.teopteop.ecommerce.domain.inventory.entity.InventoryAdjustReason;
import com.teopteop.ecommerce.domain.inventory.exception.InventoryErrorCode;
import com.teopteop.ecommerce.domain.inventory.repository.InventoryJpaRepository;
import com.teopteop.ecommerce.global.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InventoryCommandService {

    private final InventoryJpaRepository inventoryJpaRepository;

    public void restock(Long id, InventoryStockRequest request) {
        if (request.reason() != InventoryAdjustReason.RESTOCK) {
            throw new ApplicationException(InventoryErrorCode.INVALID_ADJUST_REASON);
        }

        Inventory foundInventory = inventoryJpaRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(InventoryErrorCode.INVENTORY_NOT_FOUND));

        foundInventory.increase(request.amount());
        log.info("재고가 입고되었습니다. 수량:{}, 사유:{}", request.amount(), request.reason());
    }

    public void deduct(Long id, InventoryStockRequest request) {
        if (request.reason() != InventoryAdjustReason.ADMIN_DEDUCT) {
            throw new ApplicationException(InventoryErrorCode.INVALID_ADJUST_REASON);
        }

        Inventory foundInventory = inventoryJpaRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(InventoryErrorCode.INVENTORY_NOT_FOUND));

        foundInventory.decrease(request.amount());
        log.info("재고가 차감되었습니다. 수량:{}, 사유:{}", request.amount(), request.reason());
    }

}
