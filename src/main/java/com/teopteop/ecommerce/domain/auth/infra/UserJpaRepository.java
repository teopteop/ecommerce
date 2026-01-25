package com.teopteop.ecommerce.domain.auth.infra;

import com.teopteop.ecommerce.domain.auth.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long> {

}
