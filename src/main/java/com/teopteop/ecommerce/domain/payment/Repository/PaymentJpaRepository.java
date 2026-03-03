package com.teopteop.ecommerce.domain.payment.Repository;

import com.teopteop.ecommerce.domain.payment.entity.Payment;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Payment p where p.orderNumber = :orderNumber")
    Optional<Payment> findByOrderNumberForUpdate(String orderNumber); // 동시성 제어 비관락
}
