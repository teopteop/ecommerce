package com.teopteop.ecommerce.domain.product.application;

import com.teopteop.ecommerce.domain.product.domain.Product;
import com.teopteop.ecommerce.domain.product.domain.ProductRepository;
import com.teopteop.ecommerce.domain.product.dto.ProductCreateRequest;
import com.teopteop.ecommerce.domain.product.dto.ProductResponse;
import com.teopteop.ecommerce.domain.product.exception.ProductErrorCode;
import com.teopteop.ecommerce.global.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponse registerProduct(ProductCreateRequest request) {
        Product product = Product.create(request.name(), request.price(), request.stockQuantity());
        return ProductResponse.fromEntity(productRepository.save(product));
    }

    @Transactional(readOnly = true)
    public ProductResponse findProduct(Long id) {
        return ProductResponse.fromEntity(productRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ProductErrorCode.PRODUCT_NOT_FOUND)));
    }

}
