package com.teopteop.ecommerce.domain.delivery.service;

import com.teopteop.ecommerce.domain.delivery.entity.Delivery;
import com.teopteop.ecommerce.domain.delivery.exception.DeliveryErrorCode;
import com.teopteop.ecommerce.domain.delivery.exception.DeliveryException;
import com.teopteop.ecommerce.domain.seller.entity.Seller;
import com.teopteop.ecommerce.domain.seller.service.SellerQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryFacade {

    private final DeliveryCommandService deliveryCommandService;
    private final DeliveryQueryService deliveryQueryService;
    private final SellerQueryService sellerQueryService;

    public void startShipping(Long id, Long accountId) {
        Seller foundSeller = sellerQueryService.findByAccountId(accountId);
        Delivery foundDelivery = deliveryQueryService.findById(id);

        if (!foundDelivery.getSellerId().equals(foundSeller.getId())) {
            throw new DeliveryException(DeliveryErrorCode.DELIVERY_FORBIDDEN);
        }

        deliveryCommandService.startShipping(id);
    }

    public void completeDelivery(Long id, Long accountId) {
        Seller foundSeller = sellerQueryService.findByAccountId(accountId);
        Delivery foundDelivery = deliveryQueryService.findById(id);

        if (!foundDelivery.getSellerId().equals(foundSeller.getId())) {
            throw new DeliveryException(DeliveryErrorCode.DELIVERY_FORBIDDEN);
        }

        deliveryCommandService.completeDelivery(id);

    }
}
