package com.teopteop.ecommerce.domain.product.service;

import com.teopteop.ecommerce.domain.product.dto.ProductAdminResponse;
import com.teopteop.ecommerce.domain.product.dto.ProductResponse;
import com.teopteop.ecommerce.domain.product.entity.Product;
import com.teopteop.ecommerce.domain.product.entity.ProductStatus;
import com.teopteop.ecommerce.domain.product.exception.ProductErrorCode;
import com.teopteop.ecommerce.domain.product.repository.ProductJpaRepository;
import com.teopteop.ecommerce.domain.product.repository.ProductQueryRepository;
import com.teopteop.ecommerce.global.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductQueryService {

    private final ProductJpaRepository productJpaRepository;
    private final ProductQueryRepository productQueryRepository;

    public ProductResponse findProduct(Long id) {
        return ProductResponse.fromEntity(productJpaRepository.findByIdAndDeletedFalseAndStatus(id, ProductStatus.SELLING)
                .orElseThrow(() -> new ApplicationException(ProductErrorCode.PRODUCT_NOT_FOUND)));
    }

    public Page<ProductResponse> findProducts(Pageable pageable) {

        Page<Product> products = productJpaRepository.findByStatusAndDeletedFalse(ProductStatus.SELLING, pageable);
        return products.map(ProductResponse::fromEntity);
    }

    public ProductAdminResponse findProductWithDeleted(Long id) {
        // 임시 데이터 하드코딩 Inventory, Category 도메인 설계 후 교체 예정
        // QueryDSL 프로젝션으로 조합할 예정
        int stockQuantity = 2;
        String categoryName = "카테고리";
        Long categoryId = 1L;

        Product findProduct = productJpaRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ProductErrorCode.PRODUCT_NOT_FOUND));

        return ProductAdminResponse.of(findProduct, stockQuantity, categoryName, categoryId);
    }

    public Page<ProductAdminResponse> findProductsWithDeleted(Pageable pageable) {
        // 임시 데이터 하드코딩 Inventory, Category 도메인 설계 후 교체 예정
        // QueryDSL 프로젝션으로 조합할 예정
        int stockQuantity = 2;
        String categoryName = "카테고리";
        Long categoryId = 1L;

        Page<Product> products = productJpaRepository.findAll(pageable);
        return products.map(product -> ProductAdminResponse.of(product,stockQuantity, categoryName, categoryId));
    }

}
