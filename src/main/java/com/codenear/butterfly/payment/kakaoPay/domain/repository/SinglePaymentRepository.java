package com.codenear.butterfly.payment.kakaoPay.domain.repository;

import com.codenear.butterfly.payment.domain.SinglePayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SinglePaymentRepository extends JpaRepository<SinglePayment, Long> {
}
