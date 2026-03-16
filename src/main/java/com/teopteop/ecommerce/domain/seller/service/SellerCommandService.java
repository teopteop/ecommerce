package com.teopteop.ecommerce.domain.seller.service;

import com.teopteop.ecommerce.domain.seller.entity.BankAccount;
import com.teopteop.ecommerce.domain.seller.entity.BankCode;
import com.teopteop.ecommerce.domain.seller.entity.Seller;
import com.teopteop.ecommerce.domain.seller.exception.SellerErrorCode;
import com.teopteop.ecommerce.domain.seller.exception.SellerException;
import com.teopteop.ecommerce.domain.seller.repository.BankAccountJpaRepository;
import com.teopteop.ecommerce.domain.seller.repository.SellerJpaRepository;
import com.teopteop.ecommerce.domain.seller.vo.BusinessInfo;
import com.teopteop.ecommerce.global.common.vo.Address;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SellerCommandService {

    private final SellerJpaRepository sellerJpaRepository;
    private final BankAccountJpaRepository bankAccountJpaRepository;

    public Seller registerSeller(Long accountId, BusinessInfo businessInfo, String phoneNumber, Address address) {
        return sellerJpaRepository.save(
                Seller.create(
                        accountId,
                        businessInfo,
                        phoneNumber,
                        address
                )
        );
    }

    public BankAccount registerBankAccount(Long sellerId, String bankCode, String accountNumber, String holderName) {
        // 은행코드 유효성 검사
        BankCode.fromCode(bankCode);

        return bankAccountJpaRepository.save(
                BankAccount.create(
                        sellerId,
                        bankCode,
                        accountNumber,
                        holderName
                )
        );
    }

    public void approveSeller(Long id) {
        Seller foundSeller = sellerJpaRepository.findById(id)
                .orElseThrow(() -> new SellerException(SellerErrorCode.SELLER_NOT_FOUND));
        foundSeller.activate();
    }
}
