package com.teopteop.ecommerce.domain.product.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import com.teopteop.ecommerce.domain.category.entity.QCategory;
import com.teopteop.ecommerce.domain.inventory.entity.QInventory;
import com.teopteop.ecommerce.domain.product.dto.ProductAdminResponse;
import com.teopteop.ecommerce.domain.product.dto.ProductAdminUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.teopteop.ecommerce.domain.category.entity.QCategory.category;
import static com.teopteop.ecommerce.domain.inventory.entity.QInventory.inventory;
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

    public Optional<ProductAdminResponse> findProductByAdmin(Long id) {
        return Optional.ofNullable(
                queryFactory
                        .select(Projections.constructor(ProductAdminResponse.class,
                                product.id,
                                product.name,
                                product.price,
                                inventory.quantity,
                                product.status,
                                category.name,
                                category.id,
                                product.deleted
                        ))
                        .from(product)
                        .join(inventory).on(inventory.productId.eq(product.id))
                        .join(category).on(category.id.eq(product.categoryId))
                        .where(product.id.eq(id))
                        .fetchOne()
        );
    }

    public Page<ProductAdminResponse> findProductsByAdmin(Pageable pageable) {
        List<ProductAdminResponse> content = queryFactory
                .select(Projections.constructor(ProductAdminResponse.class,
                        product.id,
                        product.name,
                        product.price,
                        inventory.quantity,
                        product.status,
                        category.name,
                        category.id,
                        product.deleted
                ))
                .from(product)
                .join(inventory).on(inventory.productId.eq(product.id))
                .join(category).on(category.id.eq(product.categoryId))
                .fetch();

        Long total = Optional.ofNullable( // NPE 방지
                        queryFactory
                                .select(product.count())
                                .from(product)
                                .fetchOne()
                )
                .orElse(0L);

        return new PageImpl<>(content, pageable, total);
    }

}
