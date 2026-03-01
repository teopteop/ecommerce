package com.teopteop.ecommerce.domain.category.service;

import com.teopteop.ecommerce.domain.category.dto.CategoryResponse;
import com.teopteop.ecommerce.domain.category.entity.Category;
import com.teopteop.ecommerce.domain.category.exception.CategoryErrorCode;
import com.teopteop.ecommerce.domain.category.repository.CategoryJpaRepository;
import com.teopteop.ecommerce.global.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryQueryService {

    private final CategoryJpaRepository categoryJpaRepository;

    public CategoryResponse findCategory(Long id) {
        return CategoryResponse.fromEntity(categoryJpaRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ApplicationException(CategoryErrorCode.CATEGORY_NOT_FOUND)));
    }

    public CategoryResponse findCategoryWithDeleted(Long id) {
        return CategoryResponse.fromEntity(categoryJpaRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(CategoryErrorCode.CATEGORY_NOT_FOUND)));
    }

    public Page<CategoryResponse> findCategories(Pageable pageable) {
        Page<Category> foundCategories = categoryJpaRepository.findCategoriesByDeletedFalse(pageable);
        return foundCategories.map(CategoryResponse::fromEntity);
    }

}
