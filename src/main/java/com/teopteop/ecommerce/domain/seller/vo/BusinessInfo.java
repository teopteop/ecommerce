package com.teopteop.ecommerce.domain.seller.vo;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class BusinessInfo {

    private String businessName;
    private String businessNumber;
    private String representativeName;

    public BusinessInfo(
            String businessName,
            String businessNumber,
            String representativeName
    ) {
        this.businessName = businessName;
        this.businessNumber = businessNumber;
        this.representativeName = representativeName;
    }
}
