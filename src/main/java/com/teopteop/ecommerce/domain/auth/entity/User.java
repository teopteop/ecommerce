package com.teopteop.ecommerce.domain.auth.entity;

import com.teopteop.ecommerce.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 30)
    private String username;

    @Column(nullable = false, length = 100)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @Column(nullable = false)
    private Long memberId;

    private User(
            String username,
            String encodedPassword,
            UserRole role,
            Long memberId
    ) {
        this.username = username;
        this.password = encodedPassword;
        this.role = role;
        this.status = UserStatus.ACTIVE;
        this.memberId = memberId;
    }

    public static User create(
            String username,
            String encodedPassword,
            UserRole role,
            Long memberId
    ) {
        return new User(username, encodedPassword, role, memberId);
    }
}
