package com.teopteop.ecommerce.domain.order.entity;

import com.teopteop.ecommerce.global.common.vo.Address;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Delivery {

    private String receiverName;
    private String phoneNumber;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    // 배송 시점의 주소를 스냅샷으로 보존하기 위해 Address(Value Object) 값을 복사한다.
    private Delivery(
            String receiverName,
            String phoneNumber,
            Address address
    ) {
        this.receiverName = receiverName;
        this.phoneNumber = phoneNumber;
        this.address = new Address(
                address.getCity(),
                address.getStreet(),
                address.getZipcode()
        );
        this.status = DeliveryStatus.READY;
    }

    public static Delivery of(
            String receiverName,
            String phoneNumber,
            Address address
    ) {
        return new Delivery(receiverName, phoneNumber, address);
    }

}
