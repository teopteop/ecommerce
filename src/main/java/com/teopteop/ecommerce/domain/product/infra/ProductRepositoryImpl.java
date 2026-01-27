package com.teopteop.ecommerce.domain.product.infra;

import com.teopteop.ecommerce.domain.product.domain.Product;
import com.teopteop.ecommerce.domain.product.domain.ProductRepository;
import com.teopteop.ecommerce.domain.product.domain.ProductStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository jpaRepository;

    @Override
    public Product save(Product product) {
        return jpaRepository.save(product);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Page<Product> findByStatusAndDeletedFalse(ProductStatus status, Pageable pageable) {
        return jpaRepository.findByStatusAndDeletedFalse(status, pageable);
    }

    @Override
    public Page<Product> findAllByDeletedFalse(Pageable pageable) {
        return jpaRepository.findAllByDeletedFalse(pageable);
    }
}
