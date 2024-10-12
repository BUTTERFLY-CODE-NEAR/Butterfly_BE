package com.codenear.butterfly.kakaoPay.domain.repository;

import com.codenear.butterfly.kakaoPay.domain.SinglePayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SinglePaymentRepository extends JpaRepository<SinglePayment, Long> {
}
