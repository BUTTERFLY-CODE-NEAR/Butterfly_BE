package com.codenear.butterfly.kakaoPay.domain.repository;

import com.codenear.butterfly.kakaoPay.domain.OrderDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Long> {
}
