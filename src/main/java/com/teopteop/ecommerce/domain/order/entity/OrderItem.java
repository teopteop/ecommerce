package com.teopteop.ecommerce.domain.order.entity;

import com.teopteop.ecommerce.domain.order.exception.OrderException;
import com.teopteop.ecommerce.domain.order.exception.OrderItemErrorCode;
import com.teopteop.ecommerce.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OrderItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "product_name", nullable = false)
    private String productName; // 주문 당시 상품명 (스냅샷)

    @Column(name = "order_price", nullable = false, precision = 19, scale = 0)
    private BigDecimal orderPrice; // 주문 당시 가격 (스냅샷)

    @Column(nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    private OrderItemStatus status;

    private OrderItem(
            Long sellerId,
            Long productId,
            String productName,
            BigDecimal orderPrice,
            int quantity
    ) {
        this.sellerId = sellerId;
        this.productId = productId;
        this.productName = productName;
        this.orderPrice = orderPrice;
        this.quantity = quantity;
        this.status = OrderItemStatus.ORDERED;
    }

    public static OrderItem create(
            Long sellerId,
            Long productId,
            String productName,
            BigDecimal orderPrice,
            int quantity
    ) {
        return new OrderItem(sellerId, productId, productName, orderPrice, quantity);
    }

    /**
     * 연관관계 매핑 메서드
     * package-private 캡슐화
     */
    void attachToOrder(Order order) {
        this.order = order;
    }

    public void cancel() {
        if (this.status == OrderItemStatus.CANCELED) {
            throw new OrderException(OrderItemErrorCode.INVALID_STATUS_TRANSITION);
        }

        this.status = OrderItemStatus.CANCELED;
    }
}
