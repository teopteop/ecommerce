package com.teopteop.ecommerce.domain.member.service;

import com.teopteop.ecommerce.domain.member.entity.Member;
import com.teopteop.ecommerce.domain.member.exception.MemberErrorCode;
import com.teopteop.ecommerce.domain.member.repository.MemberJpaRepository;
import com.teopteop.ecommerce.global.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberQueryService {

    private final MemberJpaRepository memberJpaRepository;

    public Member findById(Long id) {
        return memberJpaRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    public boolean existsByEmail(String email) {
        return memberJpaRepository.existsByEmail(email);
    }

}