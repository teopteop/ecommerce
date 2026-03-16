package com.teopteop.ecommerce.domain.seller.service;

import com.teopteop.ecommerce.domain.seller.entity.Seller;
import com.teopteop.ecommerce.domain.seller.exception.SellerErrorCode;
import com.teopteop.ecommerce.domain.seller.exception.SellerException;
import com.teopteop.ecommerce.domain.seller.repository.SellerJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SellerQueryService {

    private final SellerJpaRepository sellerJpaRepository;

    public Seller findByAccountId(Long accountId) {
        return sellerJpaRepository.findByAccountId(accountId)
                .orElseThrow(() -> new SellerException(SellerErrorCode.SELLER_NOT_FOUND));
    }
}
