package com.teopteop.ecommerce.global.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

@MappedSuperclass
@Getter
public abstract class BaseEntity extends BaseTimeEntity{

    @CreatedBy
    @Column(updatable = false, nullable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(nullable = false)
    private String updatedBy;
}

