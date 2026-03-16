package com.teopteop.ecommerce.domain.seller.entity;

import com.teopteop.ecommerce.domain.seller.exception.SellerErrorCode;
import com.teopteop.ecommerce.domain.seller.exception.SellerException;
import com.teopteop.ecommerce.domain.seller.vo.BusinessInfo;
import com.teopteop.ecommerce.global.common.entity.BaseTimeEntity;
import com.teopteop.ecommerce.global.common.vo.Address;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "sellers",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_seller_business_number",
                columnNames = "business_number"
        ) // VO unique
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Seller extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "businessName", column = @Column(name = "business_name", nullable = false, length = 100)),
            @AttributeOverride(name = "businessNumber", column = @Column(name = "business_number", nullable = false, length = 10)),
            @AttributeOverride(name = "representativeName", column = @Column(name = "representative_name", nullable = false, length = 50))
    })
    private BusinessInfo businessInfo;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "city", column = @Column(nullable = false, length = 50)),
            @AttributeOverride(name = "street", column = @Column(nullable = false, length = 100)),
            @AttributeOverride(name = "zipcode", column = @Column(nullable = false, length = 5))
    })
    private Address address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SellerStatus status;

    private Seller(
            Long accountId,
            BusinessInfo businessInfo,
            String phoneNumber,
            Address address
    ) {
        this.accountId = accountId;
        this.businessInfo = businessInfo;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.status = SellerStatus.PENDING;
    }

    public static Seller create(
            Long accountId,
            BusinessInfo businessInfo,
            String phoneNumber,
            Address address
    ) {
        return new Seller(
                accountId,
                businessInfo,
                phoneNumber,
                address
        );
    }

    // === 상태전이 메서드 ===
    public void activate() {
        if (this.status != SellerStatus.PENDING) {
            throw new SellerException(SellerErrorCode.INVALID_STATUS_TRANSITION);
        }

        this.status = SellerStatus.ACTIVE;
    }

    public void suspend() {
        this.status = SellerStatus.SUSPENDED;
    }
}
