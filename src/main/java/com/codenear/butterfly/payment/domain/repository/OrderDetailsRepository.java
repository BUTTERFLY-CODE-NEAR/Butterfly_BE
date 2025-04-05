package com.codenear.butterfly.payment.domain.repository;

import com.codenear.butterfly.payment.domain.OrderDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Long> {
    List<OrderDetails> findAllByMemberId(Long memberId);

    OrderDetails findByOrderCode(String orderCode);
}
