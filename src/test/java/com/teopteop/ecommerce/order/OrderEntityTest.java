package com.teopteop.ecommerce.order;

import com.teopteop.ecommerce.domain.member.entity.Address;
import com.teopteop.ecommerce.domain.member.entity.Member;
import com.teopteop.ecommerce.domain.order.entity.Order;
import com.teopteop.ecommerce.domain.order.entity.OrderItem;
import com.teopteop.ecommerce.domain.product.entity.Product;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class OrderEntityTest {

    @Autowired
    EntityManager em;

    @Test
    public void orderItem매핑테스트() throws Exception {
        //given
        Member member = Member.create(
                "member1",
                "email",
                "phoneNumber",
                new Address("city", "street", "zipcode")
        );
        em.persist(member);

        Product product1 = Product.create("productA", new BigDecimal("1000"),10);
        Product product2 = Product.create("productB", new BigDecimal("1000"),10);
        em.persist(product1);
        em.persist(product2);

        OrderItem item1 = OrderItem.create(product1, 3);
        OrderItem item2 = OrderItem.create(product2, 2);

        Order order = Order.create(member);
        order.addOrderItems(List.of(item1, item2));
        em.persist(order);
        em.flush();
        em.clear();

        //when
        Order findOrder = em.find(Order.class, order.getId());

        //then
        assertThat(findOrder.getMember().getEmail()).isEqualTo(member.getEmail());
        assertThat(findOrder.getItems().size()).isEqualTo(2);
        assertThat(findOrder.getItems().get(0).getQuantity()).isEqualTo(item1.getQuantity());
        assertThat(findOrder.getItems().get(1).getQuantity()).isEqualTo(item2.getQuantity());

    }
}
