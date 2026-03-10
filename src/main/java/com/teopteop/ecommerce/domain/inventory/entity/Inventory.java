package com.teopteop.ecommerce.domain.inventory.entity;

import com.teopteop.ecommerce.domain.inventory.exception.InventoryErrorCode;
import com.teopteop.ecommerce.domain.inventory.exception.InventoryException;
import com.teopteop.ecommerce.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "inventories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Inventory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name= "product_id",unique = true, nullable = false)
    private Long productId;

    @Column(nullable = false)
    private int quantity;

    // 성능 이슈 방지: 직접 접근 금지, 로그 조회는 별도 쿼리 사용할 것
    @OneToMany(mappedBy = "inventory", cascade = CascadeType.PERSIST)
    private final List<InventoryHistory> histories = new ArrayList<>();

    private Inventory(Long productId, int quantity) {
        if (productId == null) {
            throw new InventoryException(InventoryErrorCode.INVALID_PRODUCT_ID);
        }

        if (quantity < 0 ) {
            throw new InventoryException(InventoryErrorCode.STOCK_QUANTITY_POSITIVE);
        }

        this.productId = productId;
        this.quantity = quantity;
    }

    public static Inventory create(Long productId, int quantity) {
        return new Inventory(productId, quantity);
    }

    public void increase(int quantity) {
        if (quantity <= 0) {
            throw new InventoryException(InventoryErrorCode.INVALID_INVENTORY_QUANTITY);
        }

        this.quantity += quantity;
    }

    public void decrease(int quantity) {
        if (quantity <= 0) {
            throw new InventoryException(InventoryErrorCode.INVALID_INVENTORY_QUANTITY);
        }

        if (this.quantity < quantity) {
            throw new InventoryException(InventoryErrorCode.OUT_OF_STOCK);
        }

        this.quantity -= quantity;
    }

    public boolean isOutOfStock() {
        return this.quantity == 0;
    }

    // 연관관계 편의 메서드
    public void addHistory(int adjustedQuantity, InventoryAdjustReason reason) {
        this.histories.add(InventoryHistory.create(this, adjustedQuantity, reason));
    }
}