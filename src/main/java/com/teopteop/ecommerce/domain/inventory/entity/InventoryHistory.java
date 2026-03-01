package com.teopteop.ecommerce.domain.inventory.entity;

import com.teopteop.ecommerce.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Table(name = "inventory_histories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class InventoryHistory extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id", nullable = false)
    private Inventory inventory;

    @Column(name = "adjusted_quantity", nullable = false)
    private int adjustedQuantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InventoryAdjustReason reason;

    private InventoryHistory(
            Inventory inventory,
            int adjustedQuantity,
            InventoryAdjustReason reason
    ) {
        this.inventory = inventory;
        this.adjustedQuantity = adjustedQuantity;
        this.reason = reason;
    }

    public static InventoryHistory create(
            Inventory inventory,
            int adjustedQuantity,
            InventoryAdjustReason reason
    ) {
        return new InventoryHistory(inventory, adjustedQuantity, reason);
    }

}
