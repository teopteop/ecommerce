package com.teopteop.ecommerce.domain.member.domain;

import com.teopteop.ecommerce.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(unique = true, nullable = false, length = 50)
    private String email;

    @Column(nullable = false, length = 20)
    private String phoneNumber;

    @Embedded
    private Address address;

    private Member(
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

    public static Member create(
            String name,
            String email,
            String phoneNumber,
            Address address
    ) {
        return new Member(name, email, phoneNumber, address);
    }
}
