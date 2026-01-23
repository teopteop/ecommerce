package com.teopteop.ecommerce.domain.order.entity;

import com.teopteop.ecommerce.domain.product.entity.Product;
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
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    @Column(nullable = false)
    private BigDecimal orderPrice; // 주문 당시 가격

    @Column(nullable = false)
    private int quantity;

    private OrderItem(Product product, int quantity) {
        this.product = product;
        this.orderPrice = product.getPrice();
        this.quantity = quantity;
    }

    public static OrderItem create(Product product, int quantity) {
        return new OrderItem(product, quantity);
    }

    /**
     * 연관관계 매핑 메서드
     * package-private 캡슐화
     */
    void attachToOrder(Order order) {
        this.order = order;
    }
}
