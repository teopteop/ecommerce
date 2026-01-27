package com.teopteop.ecommerce.domain.product.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findById(Long id);

    Page<Product> findByStatusAndDeletedFalse(ProductStatus status, Pageable pageable);

    Page<Product> findAllByDeletedFalse(Pageable pageable);
}
