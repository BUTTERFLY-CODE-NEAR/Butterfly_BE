package com.codenear.butterfly.kakaoPay.domain.repository;

import com.codenear.butterfly.kakaoPay.domain.OrderDetails;
import com.codenear.butterfly.kakaoPay.domain.dto.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Long> {
    List<OrderDetails> findAllByMemberId(Long memberId);

    OrderDetails findByOrderCode(String orderCode);

    Page<OrderDetails> findByOrderStatus(OrderStatus status, Pageable pageable);

    @Modifying
    @Query("UPDATE OrderDetails o SET o.orderStatus = :newStatus WHERE o.id IN :orderIds AND o.orderStatus = :currentStatus")
    int updateOrderStatusInBulk(@Param("orderIds") List<Long> orderIds,
                                @Param("currentStatus") OrderStatus currentStatus,
                                @Param("newStatus") OrderStatus newStatus);
}
