package com.teopteop.ecommerce.global.security.principal;

import com.teopteop.ecommerce.domain.account.entity.AccountRole;
import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class AccountPrincipal implements UserDetails {

    private final Long id;
    private final Long customerId;
    private final String username;
    @Nullable private final String password;
    private final AccountRole role;

    public AccountPrincipal(
            Long id,
            Long customerId,
            String username,
            @Nullable String password,
            AccountRole role
    ) {
        this.id = id;
        this.customerId = customerId;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // === UserDetails Override
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    /*
     * JWT 기반 인증에서는 토큰 발급 시점에서 계정 상태를 모두 확인함
     * 따라서 불필요한 메서드들은 true로 반환값 명시
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
