package com.teopteop.ecommerce.domain.product.infra;

import com.teopteop.ecommerce.domain.product.domain.Product;
import com.teopteop.ecommerce.domain.product.domain.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductJpaRepository extends JpaRepository<Product, Long> {

    Page<Product> findByStatusAndDeletedFalse(ProductStatus status, Pageable pageable);

    Page<Product> findAllByDeletedFalse(Pageable pageable);
}
