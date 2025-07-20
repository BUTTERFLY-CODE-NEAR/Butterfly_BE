package com.codenear.butterfly.payment.domain.repository;

import com.codenear.butterfly.payment.domain.OrderDetails;
import com.codenear.butterfly.payment.domain.dto.OrderStatus;
import com.codenear.butterfly.payment.domain.dto.OrderType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Long> {
    List<OrderDetails> findAllByMemberId(Long memberId);

    OrderDetails findByOrderCode(String orderCode);

    Page<OrderDetails> findByOrderStatus(OrderStatus status, Pageable pageable);

    Page<OrderDetails> findByOrderStatusAndOrderType(OrderStatus status, Pageable pageable, OrderType orderType);

    Page<OrderDetails> findAllByOrderType(OrderType orderType, Pageable pageable);

    @Modifying
    @Query("UPDATE OrderDetails o SET o.orderStatus = :newStatus WHERE o.id IN :orderIds")
    int updateOrderStatusInBulk(@Param("orderIds") List<Long> orderIds,
                                @Param("newStatus") OrderStatus newStatus);
}
