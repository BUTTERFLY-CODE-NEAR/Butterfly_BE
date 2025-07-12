package com.codenear.butterfly.payment.kakaoPay.domain.repository;

import com.codenear.butterfly.payment.domain.SinglePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SinglePaymentRepository extends JpaRepository<SinglePayment, Long> {
    @Query(value = "SELECT sp.provider FROM single_payment sp WHERE sp.tid = :tid", nativeQuery = true)
    String findProviderByTid(@Param(value = "tid") String tid);
}
