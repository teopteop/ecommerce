package com.teopteop.ecommerce.domain.account.entity;

import com.teopteop.ecommerce.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "accounts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Account extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;

    private Account(
            String email,
            String encodedPassword,
            AccountRole role
    ) {
        this.email = email;
        this.password = encodedPassword;
        this.role = role;
        this.status = AccountStatus.ACTIVE;
    }

    public static Account create(
            String email,
            String encodedPassword,
            AccountRole role
    ) {
        return new Account(email, encodedPassword, role);
    }
}
