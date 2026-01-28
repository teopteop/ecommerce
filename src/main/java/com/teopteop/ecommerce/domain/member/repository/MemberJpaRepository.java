package com.teopteop.ecommerce.domain.member.repository;

import com.teopteop.ecommerce.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {

    boolean existsByEmail(String email);

}
