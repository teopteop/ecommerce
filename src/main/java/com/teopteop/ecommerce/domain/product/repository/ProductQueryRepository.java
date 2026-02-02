package com.teopteop.ecommerce.domain.product.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import com.teopteop.ecommerce.domain.product.dto.ProductAdminUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.teopteop.ecommerce.domain.product.entity.QProduct.product;

@Repository
@RequiredArgsConstructor
public class ProductQueryRepository {

    private final JPAQueryFactory queryFactory;

    public long updateProductDynamic(Long id, ProductAdminUpdateRequest request) {
        JPAUpdateClause update = queryFactory.update(product)
                .where(product.id.eq(id));

        if (request.name() != null) {
            update.set(product.name, request.name());
        }
        if (request.price() != null) {
            update.set(product.price, request.price());
        }
        if (request.status() != null) {
            update.set(product.status, request.status());
        }

        return update.execute();
    }

}
