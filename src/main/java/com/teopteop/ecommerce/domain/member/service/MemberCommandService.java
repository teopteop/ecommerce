package com.teopteop.ecommerce.domain.member.service;

import com.teopteop.ecommerce.domain.member.entity.Member;
import com.teopteop.ecommerce.domain.member.repository.MemberJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberCommandService {

    private final MemberJpaRepository memberJpaRepository;

    public Member registerMember(Member member) {
        return memberJpaRepository.save(member);
    }

}