package com.teopteop.ecommerce.domain.product.service;

import com.teopteop.ecommerce.domain.product.dto.ProductAdminUpdateRequest;
import com.teopteop.ecommerce.domain.product.dto.ProductCreateResponse;
import com.teopteop.ecommerce.domain.product.entity.Product;
import com.teopteop.ecommerce.domain.product.exception.ProductErrorCode;
import com.teopteop.ecommerce.domain.product.exception.ProductException;
import com.teopteop.ecommerce.domain.product.repository.ProductJpaRepository;
import com.teopteop.ecommerce.domain.product.repository.ProductQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductCommandService {

    private final ProductJpaRepository productJpaRepository;
    private final ProductQueryRepository productQueryRepository;

    public ProductCreateResponse registerProduct(Long sellerId, Long categoryId, String name, BigDecimal price) {
        Product product = Product.create(sellerId, name, price, categoryId);
        Product savedProduct = productJpaRepository.save(product);

        return new ProductCreateResponse(savedProduct.getId());
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
