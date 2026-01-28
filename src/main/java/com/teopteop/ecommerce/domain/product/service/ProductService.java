package com.teopteop.ecommerce.domain.product.service;

import com.teopteop.ecommerce.domain.product.dto.ProductCreateRequest;
import com.teopteop.ecommerce.domain.product.dto.ProductResponse;
import com.teopteop.ecommerce.domain.product.entity.Product;
import com.teopteop.ecommerce.domain.product.exception.ProductErrorCode;
import com.teopteop.ecommerce.domain.product.repository.ProductJpaRepository;
import com.teopteop.ecommerce.global.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductJpaRepository productJpaRepository;

    public ProductResponse registerProduct(ProductCreateRequest request) {
        Product product = Product.create(request.name(), request.price(), request.stockQuantity());
        return ProductResponse.fromEntity(productJpaRepository.save(product));
    }

    @Transactional(readOnly = true)
    public ProductResponse findProduct(Long id) {
        return ProductResponse.fromEntity(productJpaRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ProductErrorCode.PRODUCT_NOT_FOUND)));
    }

}
