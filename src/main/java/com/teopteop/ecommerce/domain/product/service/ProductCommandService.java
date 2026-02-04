package com.teopteop.ecommerce.domain.product.service;

import com.teopteop.ecommerce.domain.inventory.entity.Inventory;
import com.teopteop.ecommerce.domain.inventory.repository.InventoryJpaRepository;
import com.teopteop.ecommerce.domain.product.dto.ProductAdminCreateRequest;
import com.teopteop.ecommerce.domain.product.dto.ProductAdminCreateResponse;
import com.teopteop.ecommerce.domain.product.dto.ProductAdminResponse;
import com.teopteop.ecommerce.domain.product.dto.ProductAdminUpdateRequest;
import com.teopteop.ecommerce.domain.product.entity.Product;
import com.teopteop.ecommerce.domain.product.event.ProductCreatedEvent;
import com.teopteop.ecommerce.domain.product.exception.ProductErrorCode;
import com.teopteop.ecommerce.domain.product.repository.ProductJpaRepository;
import com.teopteop.ecommerce.domain.product.repository.ProductQueryRepository;
import com.teopteop.ecommerce.global.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductCommandService {

    private final ProductJpaRepository productJpaRepository;
    private final ProductQueryRepository productQueryRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ProductAdminCreateResponse registerProduct(ProductAdminCreateRequest request) {
        Product product = Product.create(request.name(), request.price(), request.categoryId());
        Product savedProduct = productJpaRepository.save(product);

        eventPublisher.publishEvent(new ProductCreatedEvent(savedProduct.getId(), request.stockQuantity(), request.categoryId()));
        return new ProductAdminCreateResponse(savedProduct.getId());
    }

    public void markProductDeleted(Long id) {
        Product findProduct = productJpaRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ApplicationException(ProductErrorCode.PRODUCT_NOT_FOUND));

        findProduct.markDeleted();
    }

    public void updateProduct(Long id, ProductAdminUpdateRequest request) {
        if (request.name() == null && request.status() == null && request.status() == null) {
            throw new ApplicationException(ProductErrorCode.INVALID_UPDATE_REQUEST);
        }
      
        long updateRows = productQueryRepository.updateProductDynamic(id, request);

        if(updateRows == 0) {
            throw new ApplicationException(ProductErrorCode.PRODUCT_NOT_FOUND);
        }

    }
}
