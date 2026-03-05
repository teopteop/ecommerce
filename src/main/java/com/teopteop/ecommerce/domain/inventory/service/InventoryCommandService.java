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

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InventoryCommandService {

    private final InventoryJpaRepository inventoryJpaRepository;

    // 관리자 재고 입고
    public void restock(Long id, InventoryStockRequest request) {
        if (request.reason() != InventoryAdjustReason.RESTOCK) {
            throw new ApplicationException(InventoryErrorCode.INVALID_ADJUST_REASON);
        }

        Inventory foundInventory = inventoryJpaRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(InventoryErrorCode.INVENTORY_NOT_FOUND));

        foundInventory.increase(request.amount());
        foundInventory.addHistory(request.amount(), request.reason());
    }

    /**
     * 관리자 수동 재고 차감
     * 관리자에 의한 단일 요청이므로 동시 접근 가능성이 낮아 별도 락을 적용하지 않음
     * 동시성 이슈가 발생하더라도 관리자가 직접 확인 후 재조정 가능한 운영 영역
     */
    public void deductByAdmin(Long id, InventoryStockRequest request) {
        if (request.reason() != InventoryAdjustReason.ADMIN_DEDUCT) {
            throw new ApplicationException(InventoryErrorCode.INVALID_ADJUST_REASON);
        }

        Inventory foundInventory = inventoryJpaRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(InventoryErrorCode.INVENTORY_NOT_FOUND));

        foundInventory.decrease(request.amount());
        foundInventory.addHistory(request.amount(), request.reason());
    }

    /**
     * 주문 시 재고 차감
     * 다수의 사용자가 동시에 같은 상품을 주문할 수 있으므로 비관적 락 적용
     * productId 오름차순 정렬로 모든 트랜잭션이 동일한 순서로 락을 획득하도록 강제
     * reason은 항상 ORDER로 고정
     */
    public void deductForOrder(List<Long> productIds, Map<Long, Integer> quantities) {
        // 순환대기 방지를 위한 상품ID 오름차순 정렬
        List<Inventory> foundInventories = inventoryJpaRepository
                .findByProductIdsWithLock(productIds.stream().sorted().toList());

        // 재고 없는 상품, 등록 안된 상품이 있는지 확인
        if (foundInventories.size() != productIds.size()) {
            throw new ApplicationException(InventoryErrorCode.INVENTORY_NOT_FOUND);
        }

        for (Inventory inventory : foundInventories) {
            Integer quantity = quantities.get(inventory.getProductId());

            inventory.decrease(quantity);
            inventory.addHistory(quantity, InventoryAdjustReason.ORDER);
        }

    }

    public Inventory registerInventory(Long productId, int stockQuantity) {
        Inventory inventory = Inventory.create(productId, stockQuantity);
        return inventoryJpaRepository.save(inventory);
    }

}
