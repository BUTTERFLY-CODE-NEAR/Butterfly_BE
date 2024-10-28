package com.codenear.butterfly.kakaoPay.domain.repository;

import com.codenear.butterfly.kakaoPay.domain.OrderDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Long> {
    List<OrderDetails> findAllByMemberId(Long memberId);
}
