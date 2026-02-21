package com.teopteop.ecommerce.domain.order.entity;

import com.teopteop.ecommerce.domain.member.entity.Member;
import com.teopteop.ecommerce.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST)
    private List<OrderItem> items = new ArrayList<>();

    @Embedded
    private Delivery delivery;

    private Order(Member member) {
        this.member = member;
        this.totalPrice = BigDecimal.ZERO;
        this.status = OrderStatus.CREATED;
    }

    public static Order create(Member member) {
        return new Order(member);
    }

    public void addOrderItems(List<OrderItem> items) {
        for (OrderItem item : items) {
            this.items.add(item);
            item.attachToOrder(this);
        }
    }
}