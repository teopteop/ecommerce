package com.teopteop.ecommerce.domain.product.service;

import com.teopteop.ecommerce.domain.category.service.CategoryQueryService;
import com.teopteop.ecommerce.domain.inventory.service.InventoryCommandService;
import com.teopteop.ecommerce.domain.product.dto.ProductCreateRequest;
import com.teopteop.ecommerce.domain.product.dto.ProductCreateResponse;
import com.teopteop.ecommerce.domain.seller.entity.Seller;
import com.teopteop.ecommerce.domain.seller.service.SellerQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductFacade {

    private final ProductCommandService productCommandService;

    private final InventoryCommandService inventoryCommandService;

    private final CategoryQueryService categoryQueryService;
    private final SellerQueryService sellerQueryService;

    public ProductCreateResponse registerProduct(Long accountId, ProductCreateRequest request) {

        categoryQueryService.validateCategoryExists(request.categoryId());

        Seller foundSeller = sellerQueryService.findByAccountId(accountId);

        ProductCreateResponse response = productCommandService.registerProduct(
                foundSeller.getId(),
                request.categoryId(),
                request.name(),
                request.price()
        );

        inventoryCommandService.registerInventory(response.id(), request.stockQuantity());

        return response;
    }
}
