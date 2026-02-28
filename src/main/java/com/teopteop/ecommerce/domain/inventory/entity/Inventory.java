package com.teopteop.ecommerce.domain.inventory.entity;

import com.teopteop.ecommerce.domain.inventory.exception.InventoryErrorCode;
import com.teopteop.ecommerce.global.common.entity.BaseTimeEntity;
import com.teopteop.ecommerce.global.exception.ApplicationException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    private Inventory(Long productId, int quantity) {
        if (productId == null) {
            throw new ApplicationException(InventoryErrorCode.INVALID_PRODUCT_ID);
        }

        if (quantity < 0 ) {
            throw new ApplicationException(InventoryErrorCode.STOCK_QUANTITY_POSITIVE);
        }

        this.productId = productId;
        this.quantity = quantity;
    }

    public static Inventory create(Long productId, int quantity) {
        return new Inventory(productId, quantity);
    }

    public void increase(int quantity) {
        if (quantity <= 0) {
            throw new ApplicationException(InventoryErrorCode.INVALID_INVENTORY_QUANTITY);
        }

        this.quantity += quantity;
    }

    public void decrease(int quantity) {
        if (quantity <= 0) {
            throw new ApplicationException(InventoryErrorCode.INVALID_INVENTORY_QUANTITY);
        }

        if (this.quantity < quantity) {
            throw new ApplicationException(InventoryErrorCode.OUT_OF_STOCK);
        }

        this.quantity -= quantity;
    }

    public boolean isOutOfStock() {
        return this.quantity == 0;
    }
}