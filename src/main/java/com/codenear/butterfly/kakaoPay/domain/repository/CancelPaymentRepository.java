package com.codenear.butterfly.kakaoPay.domain.repository;

import com.codenear.butterfly.kakaoPay.domain.CancelPayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CancelPaymentRepository extends JpaRepository<CancelPayment, Long> {
}
