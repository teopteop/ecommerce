package com.teopteop.ecommerce.domain.auth.domain;

import com.teopteop.ecommerce.domain.member.domain.Member;
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    private User(
            String username,
            String encodedPassword,
            UserRole role,
            Member member
    ) {
        this.username = username;
        this.password = encodedPassword;
        this.role = role;
        this.status = UserStatus.ACTIVE;
        this.member = member;
    }

    public static User create(
            String username,
            String encodedPassword,
            UserRole role,
            Member member
    ) {
        return new User(username, encodedPassword, role, member);
    }
}
