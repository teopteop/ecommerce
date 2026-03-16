package com.teopteop.ecommerce.domain.order.service;

import com.teopteop.ecommerce.domain.customer.entity.Customer;
import com.teopteop.ecommerce.domain.customer.service.CustomerQueryService;
import com.teopteop.ecommerce.domain.inventory.service.InventoryCommandService;
import com.teopteop.ecommerce.domain.order.dto.*;
import com.teopteop.ecommerce.domain.order.entity.Delivery;
import com.teopteop.ecommerce.domain.order.entity.Order;
import com.teopteop.ecommerce.domain.order.entity.OrderItem;
import com.teopteop.ecommerce.domain.order.entity.OrderItemStatus;
import com.teopteop.ecommerce.domain.order.exception.OrderErrorCode;
import com.teopteop.ecommerce.domain.order.exception.OrderException;
import com.teopteop.ecommerce.domain.order.exception.OrderItemErrorCode;
import com.teopteop.ecommerce.domain.order.repository.OrderJpaRepository;
import com.teopteop.ecommerce.domain.payment.service.PaymentCommandService;
import com.teopteop.ecommerce.domain.product.entity.Product;
import com.teopteop.ecommerce.domain.product.service.ProductQueryService;
import com.teopteop.ecommerce.global.common.vo.Address;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderCommandService {

    private final OrderJpaRepository orderJpaRepository;

    private final CustomerQueryService customerQueryService;
    private final ProductQueryService productQueryService;

    private final InventoryCommandService inventoryCommandService;
    private final PaymentCommandService paymentCommandService;

    public OrderCreateResponse registerOrder(Long accountId, OrderCreateRequest request) {

        // 1. Customer 조회 -> 수신자 정보
        Customer foundCustomer = customerQueryService.findByAccountId(accountId);

        // 2. 상품 ID 목록 추출 후 한 번에 조회
        List<Long> productIds = request.items().stream()
                .map(OrderItemRequest::productId)
                .toList();

        List<Product> foundProducts =
                productQueryService.findSellingProductsByIds(productIds);

        // 3. productId -> Product 맵 변환 (가격 조회용)
        Map<Long, Product> productMap = foundProducts.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        // 4. 재고 차감 (비관적 락: deductForOrder 내부에서 처리)
        // 재고 차감 실패 시 불필요한 객체 생성을 막기 위해 주문 생성보다 앞에 배치
        Map<Long, Integer> quantities = request.items().stream()
                .collect(Collectors.toMap(OrderItemRequest::productId, OrderItemRequest::quantity));

        inventoryCommandService.deductForOrder(productIds, quantities);

        // 5. Order 생성
        Order order = Order.create(accountId, UUID.randomUUID().toString());

        // 6. OrderItem 생성 및 Order에 추가
        for (OrderItemRequest item : request.items()) {
            Product product = productMap.get(item.productId());
            OrderItem orderItem = OrderItem.create(
                    product.getSellerId(),
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    item.quantity()
            );
            order.addOrderItem(orderItem);
        }

        // 7. 배송지 결정 (null일 시 Customer 기본 주소 사용)
        Address deliveryAddress = Optional.ofNullable(request.address())
                .map(a -> new Address(a.city(), a.street(), a.zipcode()))
                .orElse(foundCustomer.getAddress());

        // 8. 셀러별 그룹핑 후 Delivery 각각 생성
        Map<Long, List<OrderItem>> itemsBySeller = order.getItems().stream()
                .collect(Collectors.groupingBy(OrderItem::getSellerId));

        itemsBySeller.forEach((sellerId, sellerItems) -> {
            Delivery delivery = Delivery.create(
                    sellerId,
                    foundCustomer.getName(),
                    foundCustomer.getPhoneNumber(),
                    deliveryAddress
            );
            order.addDelivery(delivery);
        });

        // 9. 저장 (CascadeType.PERSIST로 인해 OrderItem, Delivery 함께 저장)
        Order savedOrder = orderJpaRepository.save(order);

        // 10. Payment 저장 메서드 호출
        paymentCommandService.registerPayment(savedOrder.getId(), savedOrder.getOrderNumber(), savedOrder.getTotalPrice());

        return new OrderCreateResponse(savedOrder.getId(), savedOrder.getOrderNumber());
    }

    public void cancelOrder(Long id, Long accountId, OrderCancelRequest request) {

        // 1. Order 조회
        Order foundOrder = orderJpaRepository.findWithItemsAndDeliveriesById(id)
                .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND));

        // 2. 취소 가능 여부 확인 - 본인 주문인지, 모든 상품의 배송이 취소 가능 상태인지 검증
        if (!foundOrder.getAccountId().equals(accountId)) {
            throw new OrderException(OrderErrorCode.ORDER_FORBIDDEN);
        }

        boolean cancelable = foundOrder.getDeliveries().stream()
                .allMatch(d -> d.isCancelable(LocalDateTime.now()));

        if (!cancelable) {
            throw new OrderException(OrderErrorCode.INVALID_STATUS_TRANSITION);
        }

        // 3. Order 상태 전이
        foundOrder.cancel();

        // 4. OrderItem 전체 취소
        List<OrderItem> items = foundOrder.getItems();
        items.forEach(OrderItem::cancel);

        // 5. 재고 복구
        inventoryCommandService.restoreForCancel(items);

        // 6. Payment 전액 취소
        paymentCommandService.cancelPayment(foundOrder.getOrderNumber(), request.cancelReason(), null);
    }

    public void partialCancelOrder(Long id, Long accountId, OrderPartialCancelRequest request) {

        // 1. Order 조회
        Order foundOrder = orderJpaRepository.findWithItemsAndDeliveriesById(id)
                .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND));

        // 2. 취소할 아이템 필터링
        List<OrderItem> itemsToCancel = foundOrder.getItems().stream()
                .filter(item -> request.orderItemIds().contains(item.getId()))
                .toList();

        // 3. 취소 가능 여부 확인 - 본인 주문인지, 상품의 배송이 취소 가능 상태인지 검증
        if (!foundOrder.getAccountId().equals(accountId)) {
            throw new OrderException(OrderErrorCode.ORDER_FORBIDDEN);
        }

        // 취소할 아이템의 sellerId 목록
        List<Long> sellerIds = itemsToCancel.stream()
                .map(OrderItem::getSellerId)
                .toList();

        // sellerIds에 포함된 배송의 취소 가능 여부 검증
        boolean cancelable = foundOrder.getDeliveries().stream()
                .filter(d -> sellerIds.contains(d.getSellerId()))
                .allMatch(d -> d.isCancelable(LocalDateTime.now()));

        if (!cancelable) {
            throw new OrderException(OrderErrorCode.INVALID_STATUS_TRANSITION);
        }

        if (itemsToCancel.isEmpty()) {
            throw new OrderException(OrderItemErrorCode.ORDER_ITEM_NOT_FOUND);
        }

        // 4. 아이템 취소
        itemsToCancel.forEach(OrderItem::cancel);

        // 5. 취소 금액 계산 (주문 당시 가격 * 수량 합산)
        BigDecimal cancelAmount = itemsToCancel.stream()
                .map(item -> item.getOrderPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 6. 남은 아이템 중 ORDERED 상태인 아이템이 있으면 부분취소, 없을 시 전체 취소
        boolean hasRemainingItems = foundOrder.getItems().stream()
                .anyMatch(item -> item.getStatus() == OrderItemStatus.ORDERED);

        if (hasRemainingItems) {
            foundOrder.partialCancel();
        } else {
            foundOrder.cancel();
        }

        // 7. 재고 복구
        inventoryCommandService.restoreForCancel(itemsToCancel);

        // 8. payment 부분 취소: cancelAmount가 존재여부로 판단
        paymentCommandService.cancelPayment(foundOrder.getOrderNumber(), request.cancelReason(), cancelAmount);
    }
}
