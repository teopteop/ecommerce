package com.teopteop.ecommerce.domain.category.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import com.teopteop.ecommerce.domain.category.dto.CategoryUpdateRequest;
import com.teopteop.ecommerce.domain.category.entity.QCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.teopteop.ecommerce.domain.category.entity.QCategory.*;

@Repository
@RequiredArgsConstructor
public class CategoryQueryRepository {

    private final JPAQueryFactory queryFactory;

    public long updateCategoryDynamic(Long id, CategoryUpdateRequest request) {
        JPAUpdateClause update = queryFactory.update(category)
                .where(category.id.eq(id));

        if (request.name() != null) {
            update.set(category.name, request.name());
        }

        if (request.code() != null) {
            update.set(category.code, request.code());
        }

        if (request.parentId() != null) {
            update.set(category.parentId, request.parentId());
        }

        return update.execute();
    }
}
