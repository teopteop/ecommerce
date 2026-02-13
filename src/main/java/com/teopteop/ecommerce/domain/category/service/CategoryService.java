package com.teopteop.ecommerce.domain.category.service;

import com.teopteop.ecommerce.domain.category.dto.CategoryCreateRequest;
import com.teopteop.ecommerce.domain.category.dto.CategoryCreateResponse;
import com.teopteop.ecommerce.domain.category.dto.CategoryResponse;
import com.teopteop.ecommerce.domain.category.dto.CategoryUpdateRequest;
import com.teopteop.ecommerce.domain.category.entity.Category;
import com.teopteop.ecommerce.domain.category.exception.CategoryErrorCode;
import com.teopteop.ecommerce.domain.category.repository.CategoryJpaRepository;
import com.teopteop.ecommerce.domain.category.repository.CategoryQueryRepository;
import com.teopteop.ecommerce.global.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryJpaRepository categoryJpaRepository;
    private final CategoryQueryRepository categoryQueryRepository;

    public CategoryCreateResponse registerCategory(CategoryCreateRequest request) {
        Category category = Category.create(request.name(), request.code(), request.parentId());
        Category savedCategory = categoryJpaRepository.save(category);
        return new CategoryCreateResponse(savedCategory.getId());
    }

    @Transactional(readOnly = true)
    public CategoryResponse findCategory(Long id) {
        return CategoryResponse.fromEntity(categoryJpaRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ApplicationException(CategoryErrorCode.CATEGORY_NOT_FOUND)));
    }

    @Transactional(readOnly = true)
    public CategoryResponse findCategoryWithDeleted(Long id) {
        return CategoryResponse.fromEntity(categoryJpaRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(CategoryErrorCode.CATEGORY_NOT_FOUND)));
    }

    public void deleteCategory(Long id) {
        Category foundCategory = categoryJpaRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ApplicationException(CategoryErrorCode.CATEGORY_NOT_FOUND));

        foundCategory.markDeleted();
    }

    public Page<CategoryResponse> findCategories(Pageable pageable) {
        Page<Category> foundCategories = categoryJpaRepository.findCategoriesByDeletedFalse(pageable);
        return foundCategories.map(CategoryResponse::fromEntity);
    }

    public void updateCategory(Long id, CategoryUpdateRequest request) {
        if (request.code() == null && request.name() == null && request.parentId() == null) {
            throw new ApplicationException(CategoryErrorCode.INVALID_UPDATE_REQUEST);
        }

        long updateRows = categoryQueryRepository.updateCategoryDynamic(id, request);

        if (updateRows == 0) {
            throw new ApplicationException(CategoryErrorCode.CATEGORY_NOT_FOUND);
        }
    }

}
