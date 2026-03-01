package com.teopteop.ecommerce.domain.product.service;

import com.teopteop.ecommerce.domain.product.dto.ProductAdminResponse;
import com.teopteop.ecommerce.domain.product.dto.ProductResponse;
import com.teopteop.ecommerce.domain.product.entity.Product;
import com.teopteop.ecommerce.domain.product.entity.ProductStatus;
import com.teopteop.ecommerce.domain.product.exception.ProductErrorCode;
import com.teopteop.ecommerce.domain.product.repository.ProductJpaRepository;
import com.teopteop.ecommerce.domain.product.repository.ProductQueryRepository;
import com.teopteop.ecommerce.global.common.dto.PageResponse;
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
        return productQueryRepository.findProductByAdmin(id)
                .orElseThrow(() -> new ApplicationException(ProductErrorCode.PRODUCT_NOT_FOUND));
    }

    public Page<ProductAdminResponse> findProductsWithDeleted(Pageable pageable) {
        return productQueryRepository.findProductsByAdmin(pageable);
    }

}
