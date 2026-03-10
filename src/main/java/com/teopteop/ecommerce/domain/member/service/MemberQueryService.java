package com.teopteop.ecommerce.domain.member.service;

import com.teopteop.ecommerce.domain.member.entity.Member;
import com.teopteop.ecommerce.domain.member.exception.MemberErrorCode;
import com.teopteop.ecommerce.domain.member.exception.MemberException;
import com.teopteop.ecommerce.domain.member.repository.MemberJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberQueryService {

    private final MemberJpaRepository memberJpaRepository;

    public Member findById(Long id) {
        return memberJpaRepository.findById(id)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    public void validateEmailNotDuplicate(String email) {
        if (memberJpaRepository.existsByEmail(email)) {
            throw new MemberException(MemberErrorCode.EMAIL_DUPLICATE);
        }
    }

}