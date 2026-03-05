package com.teopteop.ecommerce.domain.order.service;

import com.teopteop.ecommerce.domain.auth.entity.User;
import com.teopteop.ecommerce.domain.auth.service.UserQueryService;
import com.teopteop.ecommerce.domain.inventory.service.InventoryCommandService;
import com.teopteop.ecommerce.domain.member.entity.Member;
import com.teopteop.ecommerce.domain.member.service.MemberQueryService;
import com.teopteop.ecommerce.domain.order.dto.OrderCreateRequest;
import com.teopteop.ecommerce.domain.order.dto.OrderCreateResponse;
import com.teopteop.ecommerce.domain.order.dto.OrderItemRequest;
import com.teopteop.ecommerce.domain.order.entity.Delivery;
import com.teopteop.ecommerce.domain.order.entity.Order;
import com.teopteop.ecommerce.domain.order.entity.OrderItem;
import com.teopteop.ecommerce.domain.order.repository.OrderJpaRepository;
import com.teopteop.ecommerce.domain.payment.service.PaymentCommandService;
import com.teopteop.ecommerce.domain.product.entity.Product;
import com.teopteop.ecommerce.domain.product.service.ProductQueryService;
import com.teopteop.ecommerce.global.common.vo.Address;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private final UserQueryService userQueryService;
    private final MemberQueryService memberQueryService;
    private final ProductQueryService productQueryService;

    private final InventoryCommandService inventoryCommandService;
    private final PaymentCommandService paymentCommandService;

    public OrderCreateResponse registerOder(Long userId, OrderCreateRequest request) {

        // 1. User 조회 -> memberId 확보
        User foundUser = userQueryService.findActiveUserById(userId);

        // 2. Member 조회 -> 수신자 정보
        Member foundMember = memberQueryService.findById(foundUser.getMemberId());

        // 3. 상품 ID 목록 추출 후 한 번에 조회
        List<Long> productIds = request.items().stream()
                .map(OrderItemRequest::productId)
                .toList();

        List<Product> foundProducts =
                productQueryService.findSellingProductsByIds(productIds);

        // 4. productId -> Product 맵 변환 (가격 조회용)
        Map<Long, Product> productMap = foundProducts.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        // 5. 재고 차감 (비관적 락: deductForOrder 내부에서 처리)
        Map<Long, Integer> quantities = request.items().stream()
                .collect(Collectors.toMap(OrderItemRequest::productId, OrderItemRequest::quantity));

        inventoryCommandService.deductForOrder(productIds, quantities);

        // 6. Order 생성
        Order order = Order.create(foundUser.getMemberId(), UUID.randomUUID().toString());

        // 7. OrderItem 생성 및 Order에 추가
        for (OrderItemRequest item : request.items()) {
            Product product = productMap.get(item.productId());
            OrderItem orderItem = OrderItem.create(product.getId(), product.getPrice(), item.quantity());
            order.addOrderItem(orderItem);
        }

        // 8. 배송지 결정 (null일 시 Member 기본 주소 사용)
        Address deliveryAddress = Optional.ofNullable(request.address())
                .map(a -> new Address(a.city(), a.street(), a.zipcode()))
                .orElse(foundMember.getAddress());

        Delivery delivery = Delivery.create(foundMember.getName(), foundMember.getPhoneNumber(), deliveryAddress);
        order.linkDelivery(delivery);

        // 9. 저장 (CaseCadeType.PERSIST로 인해 OrderItem, Delivery 함께 저장)
        Order savedOrder = orderJpaRepository.save(order);

        // 10. Payment 저장 메서드 호출
        paymentCommandService.createPayment(savedOrder.getId(), savedOrder.getOrderNumber(), savedOrder.getTotalPrice());

        return new OrderCreateResponse(savedOrder.getId());
    }

    public void cancelOrder(String orderNumber) {

    }
}
