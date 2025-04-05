package com.codenear.butterfly.payment.kakaoPay.domain.repository;

import com.codenear.butterfly.payment.domain.CancelPayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CancelPaymentRepository extends JpaRepository<CancelPayment, Long> {
}
