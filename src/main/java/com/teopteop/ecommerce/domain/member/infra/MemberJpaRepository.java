package com.teopteop.ecommerce.domain.member.infra;

import com.teopteop.ecommerce.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);
}
