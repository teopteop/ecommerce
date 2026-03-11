package com.teopteop.ecommerce.domain.order.entity;

import com.teopteop.ecommerce.domain.order.exception.DeliveryErrorCode;
import com.teopteop.ecommerce.domain.order.exception.OrderErrorCode;
import com.teopteop.ecommerce.domain.order.exception.OrderException;
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

    @Column(name = "order_number", nullable = false, unique = true, updatable = false)
    private String orderNumber; // 외부 식별용 UUID

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "total_price", nullable = false, precision = 19, scale = 0)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST)
    private List<OrderItem> items = new ArrayList<>();

    @OneToOne(mappedBy = "order", cascade = CascadeType.PERSIST)
    private Delivery delivery;

    private Order(Long customerId, String orderNumber) {
        this.customerId = customerId;
        this.orderNumber = orderNumber;
        this.totalPrice = BigDecimal.ZERO;
        this.status = OrderStatus.PENDING;
    }

    public static Order create(
            Long customerId,
            String orderNumber
    ) {
        return new Order(customerId, orderNumber);
    }

    // 주문 항목 추가 및 총 금액 계산
    public void addOrderItem(OrderItem item) {
        this.items.add(item);
        item.attachToOrder(this);
        this.totalPrice = totalPrice.add(item.getOrderPrice());
    }

    // 연관관계 편의 메서드
    public void linkDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.attachToOrder(this);
    }

    // === 상태전이 메서드 ===
    public void cancel() {
        if (this.status == OrderStatus.CANCELED || this.status == OrderStatus.SHIPPED) {
            throw new OrderException(OrderErrorCode.INVALID_STATUS_TRANSITION);
        }

        this.status = OrderStatus.CANCELED;
    }

    public void partialCancel()  {
        if (this.status == OrderStatus.CANCELED || this.status == OrderStatus.SHIPPED) {
            throw new OrderException(OrderErrorCode.INVALID_STATUS_TRANSITION);
        }

        this.status = OrderStatus.PARTIAL_CANCELED;
    }

    // Payment -> Order 상태 전이
    public void markPaid() {
        if (this.status != OrderStatus.PENDING && this.status != OrderStatus.PAYMENT_FAILED) {
            throw new OrderException(OrderErrorCode.INVALID_STATUS_TRANSITION);
        }

        this.status = OrderStatus.PAID;
    }

    public void markPaymentFailed() {
        if (this.status == OrderStatus.PAYMENT_FAILED) return; // 멱등성 처리
        if (this.status != OrderStatus.PENDING) {
            throw new OrderException(OrderErrorCode.INVALID_STATUS_TRANSITION);
        }

        this.status = OrderStatus.PAYMENT_FAILED;
    }

    // Delivery 상태 전이 위임
    public void startShipping() {
        if (this.status != OrderStatus.PAID) {
            throw new OrderException(DeliveryErrorCode.INVALID_STATUS_TRANSITION);
        }

        this.delivery.ship();
        this.status = OrderStatus.SHIPPED;
    }

    public void completeDelivery() {
        if (this.status != OrderStatus.SHIPPED) {
            throw new OrderException(DeliveryErrorCode.INVALID_STATUS_TRANSITION);
        }

        this.delivery.complete();
        this.status = OrderStatus.DELIVERED;
    }
}