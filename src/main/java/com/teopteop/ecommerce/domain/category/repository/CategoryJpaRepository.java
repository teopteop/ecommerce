package com.teopteop.ecommerce.domain.category.repository;

import com.teopteop.ecommerce.domain.category.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryJpaRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByIdAndDeletedFalse(Long id);

    Page<Category> findCategoriesByDeletedFalse(Pageable pageable);
}
