package com.teopteop.ecommerce.domain.product.entity;

import com.teopteop.ecommerce.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 19, scale = 0)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;

    @Column(nullable = false)
    private Long categoryId;

    @Column(nullable = false)
    private boolean deleted;

    private Product(
            String name,
            BigDecimal price,
            Long categoryId
    ) {
        this.name = name;
        this.price = price;
        this.status = ProductStatus.STOPPED;
        this.categoryId = categoryId;
        this.deleted = false;
    }

    public static Product create(
            String name,
            BigDecimal price,
            Long categoryId
    ) {
        return new Product(name, price, categoryId);
    }

    public void markDeleted() {
        this.deleted = true;
    }
}
