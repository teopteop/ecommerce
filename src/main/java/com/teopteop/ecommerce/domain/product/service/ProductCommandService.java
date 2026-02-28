package com.teopteop.ecommerce.domain.product.service;

import com.teopteop.ecommerce.domain.category.exception.CategoryErrorCode;
import com.teopteop.ecommerce.domain.category.repository.CategoryJpaRepository;
import com.teopteop.ecommerce.domain.inventory.entity.Inventory;
import com.teopteop.ecommerce.domain.inventory.repository.InventoryJpaRepository;
import com.teopteop.ecommerce.domain.product.dto.ProductAdminCreateRequest;
import com.teopteop.ecommerce.domain.product.dto.ProductAdminCreateResponse;
import com.teopteop.ecommerce.domain.product.dto.ProductAdminUpdateRequest;
import com.teopteop.ecommerce.domain.product.entity.Product;
import com.teopteop.ecommerce.domain.product.exception.ProductErrorCode;
import com.teopteop.ecommerce.domain.product.repository.ProductJpaRepository;
import com.teopteop.ecommerce.domain.product.repository.ProductQueryRepository;
import com.teopteop.ecommerce.global.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductCommandService {

    private final ProductJpaRepository productJpaRepository;
    private final ProductQueryRepository productQueryRepository;
    private final InventoryJpaRepository inventoryJpaRepository;
    private final CategoryJpaRepository categoryJpaRepository;

    public ProductAdminCreateResponse registerProduct(ProductAdminCreateRequest request) {
        // 1. 카테고리 검증
        if (!categoryJpaRepository.existsByIdAndDeletedFalse(request.categoryId())) {
            throw new ApplicationException(CategoryErrorCode.CATEGORY_NOT_FOUND);
        }

        // 2. 상품 생성
        Product product = Product.create(request.name(), request.price(), request.categoryId());
        Product savedProduct = productJpaRepository.save(product);

        // 3. 재고 생성
        Inventory inventory = Inventory.create(savedProduct.getId(), request.stockQuantity());
        inventoryJpaRepository.save(inventory);

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

        if (updateRows == 0) {
            throw new ApplicationException(ProductErrorCode.PRODUCT_NOT_FOUND);
        }

    }
}
