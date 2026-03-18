package com.teopteop.ecommerce.domain.delivery.entity;

import com.teopteop.ecommerce.domain.delivery.exception.DeliveryErrorCode;
import com.teopteop.ecommerce.domain.order.exception.OrderException;
import com.teopteop.ecommerce.global.common.entity.BaseTimeEntity;
import com.teopteop.ecommerce.global.common.vo.Address;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "deliveries")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Delivery extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_item_id", nullable = false, unique = true)
    private Long orderItemId;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(name = "receiver_name", nullable = false)
    private String receiverName;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "city", column = @Column(nullable = false, length = 50)),
            @AttributeOverride(name = "street", column = @Column(nullable = false, length = 100)),
            @AttributeOverride(name = "zipcode", column = @Column(nullable = false, length = 5))
    })
    private Address address;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    // 배송 시점의 주소를 스냅샷으로 보존하기 위해 Address(Value Object) 값을 복사한다.
    private Delivery(
            Long orderItemId,
            Long sellerId,
            String receiverName,
            String phoneNumber,
            Address address
    ) {
        this.orderItemId = orderItemId;
        this.sellerId = sellerId;
        this.receiverName = receiverName;
        this.phoneNumber = phoneNumber;
        this.address = new Address(
                address.getCity(),
                address.getStreet(),
                address.getZipcode()
        );
        this.status = DeliveryStatus.PENDING;
    }

    public static Delivery create(
            Long orderItemId,
            Long sellerId,
            String receiverName,
            String phoneNumber,
            Address address
    ) {
        return new Delivery(
                orderItemId,
                sellerId,
                receiverName,
                phoneNumber,
                address
        );
    }

    // === 상태 전이 메서드 ===
    public void ship() {
        if (this.status != DeliveryStatus.PENDING) {
            throw new OrderException(DeliveryErrorCode.INVALID_STATUS_TRANSITION);
        }

        this.status = DeliveryStatus.SHIPPED;
        this.shippedAt = LocalDateTime.now();
    }

    public void complete() {
        if (this.status != DeliveryStatus.SHIPPED) {
            throw new OrderException(DeliveryErrorCode.INVALID_STATUS_TRANSITION);
        }

        this.status = DeliveryStatus.DELIVERED;
        this.deliveredAt = LocalDateTime.now();
    }

    /**
     * 주문 취소 가능 여부를 파악
     * @param cancelTime 취소 시도를 하는 시간을 전달, 테스트 및 정책 확장성 확보
     * @return 취소 가능 시 true, 불가하면 false
     */
    public boolean isCancelable(LocalDateTime cancelTime) {
        if (this.status == DeliveryStatus.PENDING) {
            return true;
        }

        if (this.status == DeliveryStatus.DELIVERED) {
            return deliveredAt.plusDays(7).isAfter(cancelTime); // 배송 완료 후 7일 이후에는 취소 불가능
        }

        return false; // SHIPPED : 배송중에는 취소 불가능
    }

}
