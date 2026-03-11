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

    @Column(nullable = false, length = 30)
    private String name;

    @Column(unique = true, nullable = false, length = 50)
    private String email;

    @Column(name = "phone_number",nullable = false, length = 20)
    private String phoneNumber;

    @Embedded
    private Address address;

    private Customer(
            String name,
            String email,
            String phoneNumber,
            Address address
    ) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public static Customer create(
            String name,
            String email,
            String phoneNumber,
            Address address
    ) {
        return new Customer(name, email, phoneNumber, address);
    }
}
