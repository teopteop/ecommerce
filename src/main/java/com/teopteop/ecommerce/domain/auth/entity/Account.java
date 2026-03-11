package com.teopteop.ecommerce.domain.auth.entity;

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

    @Column(unique = true, nullable = false, length = 30)
    private String username;

    @Column(nullable = false, length = 100)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;

    @Column(name= "member_id",nullable = false)
    private Long memberId;

    private Account(
            String username,
            String encodedPassword,
            AccountRole role,
            Long memberId
    ) {
        this.username = username;
        this.password = encodedPassword;
        this.role = role;
        this.status = AccountStatus.ACTIVE;
        this.memberId = memberId;
    }

    public static Account create(
            String username,
            String encodedPassword,
            AccountRole role,
            Long memberId
    ) {
        return new Account(username, encodedPassword, role, memberId);
    }
}
