package com.teopteop.ecommerce.domain.product.service;

import com.teopteop.ecommerce.domain.category.service.CategoryQueryService;
import com.teopteop.ecommerce.domain.inventory.service.InventoryCommandService;
import com.teopteop.ecommerce.domain.product.dto.ProductAdminCreateRequest;
import com.teopteop.ecommerce.domain.product.dto.ProductAdminCreateResponse;
import com.teopteop.ecommerce.domain.product.dto.ProductAdminUpdateRequest;
import com.teopteop.ecommerce.domain.product.entity.Product;
import com.teopteop.ecommerce.domain.product.exception.ProductErrorCode;
import com.teopteop.ecommerce.domain.product.exception.ProductException;
import com.teopteop.ecommerce.domain.product.repository.ProductJpaRepository;
import com.teopteop.ecommerce.domain.product.repository.ProductQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductCommandService {

    private final ProductJpaRepository productJpaRepository;
    private final ProductQueryRepository productQueryRepository;

    private final InventoryCommandService inventoryCommandService;

    private final CategoryQueryService categoryQueryService;

    public ProductAdminCreateResponse registerProduct(ProductAdminCreateRequest request) {
        // 1. 카테고리 검증
        categoryQueryService.validateCategoryExists(request.categoryId());

        // 2. 상품 생성
        Product product = Product.create(request.name(), request.price(), request.categoryId());
        Product savedProduct = productJpaRepository.save(product);

        // 3. 재고 생성
        inventoryCommandService.registerInventory(savedProduct.getId(), request.stockQuantity());

        return new ProductAdminCreateResponse(savedProduct.getId());
    }

    public void markProductDeleted(Long id) {
        Product findProduct = productJpaRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND));

        findProduct.markDeleted();
    }

    public void updateProduct(Long id, ProductAdminUpdateRequest request) {
        if (request.name() == null && request.price() == null && request.status() == null) {
            throw new ProductException(ProductErrorCode.INVALID_UPDATE_REQUEST);
        }

        long updateRows = productQueryRepository.updateProductDynamic(id, request);

        if (updateRows == 0) {
            throw new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND);
        }

    }
}
