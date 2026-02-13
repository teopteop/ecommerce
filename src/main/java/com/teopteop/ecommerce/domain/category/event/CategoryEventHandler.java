package com.teopteop.ecommerce.domain.category.event;

import com.teopteop.ecommerce.domain.category.exception.CategoryErrorCode;
import com.teopteop.ecommerce.domain.category.repository.CategoryJpaRepository;
import com.teopteop.ecommerce.domain.product.event.ProductCreatedEvent;
import com.teopteop.ecommerce.global.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class CategoryEventHandler {

    private final CategoryJpaRepository categoryJpaRepository;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleProductCreatedEvent(ProductCreatedEvent event) {
        if(!categoryJpaRepository.existsById(event.categoryId())) {
            throw new ApplicationException(CategoryErrorCode.CATEGORY_NOT_FOUND);
        }
    }
}
