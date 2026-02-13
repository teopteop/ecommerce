package com.teopteop.ecommerce.domain.inventory.event;

import com.teopteop.ecommerce.domain.inventory.entity.Inventory;
import com.teopteop.ecommerce.domain.inventory.repository.InventoryJpaRepository;
import com.teopteop.ecommerce.domain.product.event.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class InventoryEventHandler {

    private final InventoryJpaRepository inventoryJpaRepository;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleProductCreatedEvent(ProductCreatedEvent event) {
        Inventory inventory = Inventory.create(event.productId(), event.initialStockQuantity());
        inventoryJpaRepository.save(inventory);
    }
}
