package com.teopteop.ecommerce.domain.category.service;

import com.teopteop.ecommerce.domain.category.dto.CategoryCreateRequest;
import com.teopteop.ecommerce.domain.category.dto.CategoryCreateResponse;
import com.teopteop.ecommerce.domain.category.dto.CategoryUpdateRequest;
import com.teopteop.ecommerce.domain.category.entity.Category;
import com.teopteop.ecommerce.domain.category.exception.CategoryErrorCode;
import com.teopteop.ecommerce.domain.category.exception.CategoryException;
import com.teopteop.ecommerce.domain.category.repository.CategoryJpaRepository;
import com.teopteop.ecommerce.domain.category.repository.CategoryQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryCommandService {

    private final CategoryJpaRepository categoryJpaRepository;
    private final CategoryQueryRepository categoryQueryRepository;

    public CategoryCreateResponse registerCategory(CategoryCreateRequest request) {
        Category category = Category.create(request.name(), request.code(), request.parentId());
        Category savedCategory = categoryJpaRepository.save(category);
        return new CategoryCreateResponse(savedCategory.getId());
    }

    public void deleteCategory(Long id) {
        Category foundCategory = categoryJpaRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new CategoryException(CategoryErrorCode.CATEGORY_NOT_FOUND));

        foundCategory.markDeleted();
    }

    public void updateCategory(Long id, CategoryUpdateRequest request) {
        if (request.code() == null && request.name() == null && request.parentId() == null) {
            throw new CategoryException(CategoryErrorCode.INVALID_UPDATE_REQUEST);
        }

        long updateRows = categoryQueryRepository.updateCategoryDynamic(id, request);

        if (updateRows == 0) {
            throw new CategoryException(CategoryErrorCode.CATEGORY_NOT_FOUND);
        }
    }

}
