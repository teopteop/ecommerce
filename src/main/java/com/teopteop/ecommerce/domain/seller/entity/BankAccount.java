package com.teopteop.ecommerce.domain.seller.entity;

import com.teopteop.ecommerce.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bank_accounts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class BankAccount extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seller_id", nullable = false, unique = true)
    private Long sellerId;

    @Column(name = "bank_code", nullable = false, length = 3)
    private String bankCode;

    @Column(name = "account_number", nullable = false, length = 30)
    private String accountNumber;

    @Column(name = "holder_name", nullable = false, length = 50)
    private String holderName;

    public BankCode getBank() {
        return BankCode.fromCode(this.bankCode);
    }

    private BankAccount(
            Long sellerId,
            String bankCode,
            String accountNumber,
            String holderName
    ) {
        this.sellerId = sellerId;
        this.bankCode = bankCode;
        this.accountNumber = accountNumber;
        this.holderName = holderName;
    }

    public static BankAccount create(
            Long sellerId,
            String bankCode,
            String accountNumber,
            String holderName
    ) {
        return new BankAccount(sellerId, bankCode, accountNumber, holderName);
    }
}
