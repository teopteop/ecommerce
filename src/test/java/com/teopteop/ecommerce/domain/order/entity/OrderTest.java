package com.teopteop.ecommerce.domain.order.entity;

import com.teopteop.ecommerce.domain.order.exception.OrderErrorCode;
import com.teopteop.ecommerce.domain.order.exception.OrderException;
import com.teopteop.ecommerce.global.common.vo.Address;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Order 엔티티 상태전이 테스트")
public class OrderTest {

    private Order createOrder() {
        Order order = Order.create(1L, "ORDER-UUID-1");
        OrderItem item = OrderItem.create(1L, 1L, "상품명", BigDecimal.valueOf(10000), 2);
        order.addOrderItem(item);

        Address address = new Address("서울", "강남대로 1", "12345");
        Delivery delivery = Delivery.create(1L, "홍길동", "010-1234-5678", address);
        order.addDelivery(delivery);

        return order;
    }

    private Order createPaidOrder() {
        Order order = createOrder();
        order.markPaid();
        return order;
    }

    // === markPaid ===
    @Test
    @DisplayName("PENDING 상태에서 markPaid 호출 시 PAID")
    void markPaidSuccess() {
        Order order = createOrder();

        order.markPaid();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    @DisplayName("PAYMENT_FAILED 상태에서 markPaid 호출시 PAID: 결제 재시도 성공")
    void markPaidSuccessWhenPaymentFailed() {
        Order order = createOrder();
        order.markPaymentFailed();

        order.markPaid();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    @DisplayName("PAID 상태에서 markPaid 호출 시 예외")
    void markPaidFailsWhenPaid() {
        Order paidOrder = createPaidOrder();

        assertThatThrownBy(paidOrder::markPaid)
                .isInstanceOfSatisfying(OrderException.class, ex ->
                        assertThat(ex.getErrorCode()).isEqualTo(OrderErrorCode.INVALID_STATUS_TRANSITION));
    }

    @Test
    @DisplayName("CANCELED 상태에서 markPaid 호출 시 예외")
    void markPaidFilsWhenCanceled() {
        Order order = createOrder();
        order.cancel();

        assertThatThrownBy(order::markPaid)
                .isInstanceOfSatisfying(OrderException.class, ex ->
                        assertThat(ex.getErrorCode()).isEqualTo(OrderErrorCode.INVALID_STATUS_TRANSITION));
    }

    // === markPaymentFailed ===
    @Test
    @DisplayName("PENDING 상태에서 markPaymentFailed 호출 시 PAYMENT_FAILED")
    void markPaymentFailedSuccess() {
        Order order = createOrder();

        order.markPaymentFailed();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAYMENT_FAILED);
    }

    @Test
    @DisplayName("이미 PAYMENT_FAILED 상태에서 markPaymentFailed 호출 시 멱등성 통과")
    void markPaymentFailSuccessWhenAlreadyFailed() {
        Order order = createOrder();
        order.markPaymentFailed();

        assertThatCode(order::markPaymentFailed).doesNotThrowAnyException();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAYMENT_FAILED);
    }

    @Test
    @DisplayName("PAID 상태에서 markPaymentFailed 호출 시 예외")
    void markPaymentFailedFailsWhenPaid() {
        Order paidOrder = createPaidOrder();

        assertThatThrownBy(paidOrder::markPaymentFailed)
                .isInstanceOfSatisfying(OrderException.class, ex ->
                        assertThat(ex.getErrorCode()).isEqualTo(OrderErrorCode.INVALID_STATUS_TRANSITION));
    }

    // === cancel ===
    @Test
    @DisplayName("PAID 상태에서 cancel 호출 시 CANCELED")
    void cancelSuccess() {
        Order paidOrder = createPaidOrder();

        paidOrder.cancel();

        assertThat(paidOrder.getStatus()).isEqualTo(OrderStatus.CANCELED);
    }

    @Test
    @DisplayName("이미 CANCELED 상태에서 cancel 호출 시 예외")
    void cancelFailsWhenCanceled() {
        Order paidOrder = createPaidOrder();
        paidOrder.cancel();

        assertThatThrownBy(paidOrder::cancel)
                .isInstanceOfSatisfying(OrderException.class, ex ->
                        assertThat(ex.getErrorCode()).isEqualTo(OrderErrorCode.INVALID_STATUS_TRANSITION));
    }

    // === partialCancel ===
    @Test
    @DisplayName("PAID 상태에서 partialCancel 호출 시 PARTIAL_CANCELED")
    void partialCancelSuccess() {
        Order paidOrder = createPaidOrder();
        paidOrder.partialCancel();

        assertThat(paidOrder.getStatus()).isEqualTo(OrderStatus.PARTIAL_CANCELED);
    }

}
