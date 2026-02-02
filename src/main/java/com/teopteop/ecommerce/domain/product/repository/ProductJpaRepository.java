package com.teopteop.ecommerce.domain.product.repository;

import com.teopteop.ecommerce.domain.product.entity.Product;
import com.teopteop.ecommerce.domain.product.entity.ProductStatus;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductJpaRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByIdAndDeletedFalseAndStatus(Long id, ProductStatus status);

    Optional<Product> findByIdAndDeletedFalse(Long id);

    Page<Product> findByStatusAndDeletedFalse(ProductStatus status, Pageable pageable);

    Page<Product> findByDeletedFalse(Pageable pageable);

    @NonNull
    Page<Product> findAll(@NonNull Pageable pageable);

}
