package com.teopteop.ecommerce.domain.order.service;

import com.teopteop.ecommerce.domain.customer.entity.Customer;
import com.teopteop.ecommerce.domain.customer.service.CustomerQueryService;
import com.teopteop.ecommerce.domain.delivery.entity.Delivery;
import com.teopteop.ecommerce.domain.delivery.service.DeliveryCommandService;
import com.teopteop.ecommerce.domain.delivery.service.DeliveryQueryService;
import com.teopteop.ecommerce.domain.inventory.service.InventoryCommandService;
import com.teopteop.ecommerce.domain.order.dto.*;
import com.teopteop.ecommerce.domain.order.entity.Order;
import com.teopteop.ecommerce.domain.order.entity.OrderItem;
import com.teopteop.ecommerce.domain.order.exception.OrderErrorCode;
import com.teopteop.ecommerce.domain.order.exception.OrderException;
import com.teopteop.ecommerce.domain.payment.service.PaymentCommandService;
import com.teopteop.ecommerce.domain.product.entity.Product;
import com.teopteop.ecommerce.domain.product.service.ProductQueryService;
import com.teopteop.ecommerce.global.common.dto.PageResponse;
import com.teopteop.ecommerce.global.common.vo.Address;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderFacade {

    private final OrderCommandService orderCommandService;
    private final OrderQueryService orderQueryService;

    private final InventoryCommandService inventoryCommandService;
    private final DeliveryCommandService deliveryCommandService;
    private final PaymentCommandService paymentCommandService;

    private final CustomerQueryService customerQueryService;
    private final ProductQueryService productQueryService;
    private final DeliveryQueryService deliveryQueryService;

    public OrderCreateResponse registerOrder(Long accountId, OrderCreateRequest request) {
        Customer foundCustomer = customerQueryService.findByAccountId(accountId);

        List<Long> productIds = request.items().stream()
                .map(OrderItemRequest::productId)
                .toList();

        List<Product> foundProducts = productQueryService.findSellingProductsByIds(productIds);

        Map<Long, Product> productMap = foundProducts.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        Map<Long, Integer> quantities = request.items().stream()
                .collect(Collectors.toMap(OrderItemRequest::productId, OrderItemRequest::quantity));

        inventoryCommandService.deductForOrder(productIds, quantities);

        Address deliveryAddress = Optional.ofNullable(request.address())
                .map(a -> new Address(a.city(), a.street(), a.zipcode()))
                .orElse(foundCustomer.getAddress());

        Order savedOrder = orderCommandService.registerOrder(accountId, productMap, request.items());

        deliveryCommandService.registerForOrderItem(
                savedOrder.getItems(),
                foundCustomer.getName(),
                foundCustomer.getPhoneNumber(),
                deliveryAddress
        );

        paymentCommandService.registerPayment(
                savedOrder.getId(),
                savedOrder.getOrderNumber(),
                savedOrder.getTotalPrice()
        );

        return new OrderCreateResponse(savedOrder.getId(), savedOrder.getOrderNumber());
    }

    public void cancelOrder(Long id, Long accountId, OrderCancelRequest request) {
        Order foundOrder = orderQueryService.findWithItems(id);

        List<Long> itemIds = foundOrder.getItems().stream()
                .map(OrderItem::getId)
                .toList();

        List<Delivery> foundDeliveries = deliveryQueryService.findAllByOrderItemIn(itemIds);

        boolean cancelable = foundDeliveries.stream()
                .allMatch(d -> d.isCancelable(LocalDateTime.now()));
        if (!cancelable) throw new OrderException(OrderErrorCode.INVALID_STATUS_TRANSITION);

        orderCommandService.cancelOrder(id, accountId);
        inventoryCommandService.restoreForCancel(foundOrder.getItems());
        paymentCommandService.cancelFullPayment(foundOrder.getOrderNumber(), request.cancelReason());
    }

    public void partialCancelOrder(Long id, Long accountId, OrderPartialCancelRequest request) {
        Order foundOrder = orderQueryService.findWithItems(id);

        List<OrderItem> itemsToCancel = foundOrder.getItems().stream()
                .filter(item -> request.orderItemIds().contains(item.getId()))
                .toList();

        if (itemsToCancel.isEmpty()) throw new OrderException(OrderErrorCode.ORDER_NOT_FOUND);

        List<Long> cancelItemIds = itemsToCancel.stream()
                .map(OrderItem::getId)
                .toList();

        List<Delivery> foundDeliveries = deliveryQueryService.findAllByOrderItemIn(cancelItemIds);

        boolean cancelable = foundDeliveries.stream()
                .allMatch(d -> d.isCancelable(LocalDateTime.now()));
        if (!cancelable) throw new OrderException(OrderErrorCode.INVALID_STATUS_TRANSITION);

        BigDecimal cancelAmount = itemsToCancel.stream()
                .map(item -> item.getOrderPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        orderCommandService.partialCancelOrder(id, accountId, request.orderItemIds());
        inventoryCommandService.restoreForCancel(itemsToCancel);
        paymentCommandService.cancelPartialPayment(foundOrder.getOrderNumber(), request.cancelReason(), cancelAmount);
    }

    @Transactional(readOnly = true)
    public OrderDetailResponse findOrderDetail(Long id, Long accountId) {
        Order foundOrder = orderQueryService.findOrderDetail(id, accountId);

        List<Long> itemIds = foundOrder.getItems().stream()
                .map(OrderItem::getId)
                .toList();
        List<Delivery> foundDeliveries = deliveryQueryService.findAllByOrderItemIn(itemIds);

        return OrderDetailResponse.from(foundOrder, foundDeliveries);
    }

    @Transactional(readOnly = true)
    public PageResponse<OrderSummaryResponse> findMyOrders(Long accountId, Pageable pageable) {
        Page<Order> foundOrders = orderQueryService.findMyOrders(accountId, pageable);

        List<Long> itemIds = foundOrders.stream()
                .flatMap(o -> o.getItems().stream())
                .map(OrderItem::getId)
                .toList();

        List<Delivery> foundDeliveries = deliveryQueryService.findAllByOrderItemIn(itemIds);

        Map<Long, Delivery> deliveryMap = foundDeliveries.stream()
                .collect(Collectors.toMap(Delivery::getOrderItemId, d -> d));

        return PageResponse.from(foundOrders.map(o ->
                OrderSummaryResponse.from(o, o.getItems().stream()
                        .map(i -> deliveryMap.get(i.getId()))
                        .toList())));
    }

}
