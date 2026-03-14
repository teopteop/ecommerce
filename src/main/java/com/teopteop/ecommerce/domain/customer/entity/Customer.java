package com.teopteop.ecommerce.domain.customer.entity;

import com.teopteop.ecommerce.global.common.entity.BaseTimeEntity;
import com.teopteop.ecommerce.global.common.vo.Address;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "customers")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Customer extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id", nullable = false, unique = true)
    private Long accountId;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(name = "phone_number",nullable = false, length = 20)
    private String phoneNumber;

    @Embedded
    private Address address;

    private Customer(
            Long accountId,
            String name,
            String phoneNumber,
            Address address
    ) {
        this.accountId = accountId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public static Customer create(
            Long accountId,
            String name,
            String phoneNumber,
            Address address
    ) {
        return new Customer(accountId, name, phoneNumber, address);
    }
}
