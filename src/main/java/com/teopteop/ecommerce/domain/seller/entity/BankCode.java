package com.teopteop.ecommerce.domain.seller.entity;

import com.teopteop.ecommerce.domain.seller.exception.SellerErrorCode;
import com.teopteop.ecommerce.domain.seller.exception.SellerException;

import java.util.Arrays;

public enum BankCode {
    KDB("002", "산업은행"),
    IBK("003", "기업은행"),
    KB("004", "국민은행"),
    NH("011", "농협은행"),
    WOORI("020", "우리은행"),
    SC("023", "SC제일은행"),
    HANA("081", "하나은행"),
    SHINHAN("088", "신한은행"),
    K_BANK("089", "케이뱅크"),
    KAKAO("090", "카카오뱅크"),
    TOSS("092", "토스뱅크");

    private final String code;
    private final String bankName;

    BankCode(String code, String bankName) {
        this.code = code;
        this.bankName = bankName;
    }

    public String getCode() {
        return code;
    }

    public String getBankName() {
        return bankName;
    }

    public static BankCode fromCode(String code) {
        return Arrays.stream(values())
                .filter(b -> b.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new SellerException(SellerErrorCode.INVALID_CODE));
    }
}
