package com.teopteop.ecommerce.domain.order.service;

import com.teopteop.ecommerce.domain.order.dto.OrderItemRequest;
import com.teopteop.ecommerce.domain.order.entity.Order;
import com.teopteop.ecommerce.domain.order.entity.OrderItem;
import com.teopteop.ecommerce.domain.order.entity.OrderItemStatus;
import com.teopteop.ecommerce.domain.order.exception.OrderErrorCode;
import com.teopteop.ecommerce.domain.order.exception.OrderException;
import com.teopteop.ecommerce.domain.order.exception.OrderItemErrorCode;
import com.teopteop.ecommerce.domain.order.repository.OrderJpaRepository;
import com.teopteop.ecommerce.domain.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderCommandService {

    private final OrderJpaRepository orderJpaRepository;

    public Order registerOrder(Long accountId, Map<Long, Product> productMap, List<OrderItemRequest> items) {
        Order order = Order.create(accountId, UUID.randomUUID().toString());

        for (OrderItemRequest item : items) {
            Product product = productMap.get(item.productId());
            order.addOrderItem(OrderItem.create(
                    product.getSellerId(),
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    item.quantity()
            ));
        }

        return orderJpaRepository.save(order);
    }

    public void cancelOrder(Long id, Long accountId) {
        Order foundOrder = orderJpaRepository.findWithItemsById(id)
                .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND));

        if (!foundOrder.getAccountId().equals(accountId)) {
            throw new OrderException(OrderErrorCode.ORDER_FORBIDDEN);
        }

        foundOrder.cancel();
        foundOrder.getItems().forEach(OrderItem::cancel);
    }

    public void partialCancelOrder(Long id, Long accountId, List<Long> orderItemIds) {
        Order foundOrder = orderJpaRepository.findWithItemsById(id)
                .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND));

        if (!foundOrder.getAccountId().equals(accountId)) {
            throw new OrderException(OrderErrorCode.ORDER_FORBIDDEN);
        }

        List<OrderItem> itemToCancel = foundOrder.getItems().stream()
                .filter(item -> orderItemIds.contains(item.getId()))
                .toList();

        if (itemToCancel.isEmpty()) {
            throw new OrderException(OrderItemErrorCode.ORDER_ITEM_NOT_FOUND);
        }

        itemToCancel.forEach(OrderItem::cancel);

        boolean hasRemainingItems = foundOrder.getItems().stream()
                .anyMatch(item -> item.getStatus() == OrderItemStatus.ORDERED);

        if (hasRemainingItems) {
            foundOrder.partialCancel();
        } else {
            foundOrder.cancel();
        }
    }
}
