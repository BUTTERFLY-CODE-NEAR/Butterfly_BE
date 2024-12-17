package com.codenear.butterfly.admin.order.application;

import com.codenear.butterfly.admin.order.exception.OrderException;
import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.kakaoPay.domain.OrderDetails;
import com.codenear.butterfly.kakaoPay.domain.dto.OrderStatus;
import com.codenear.butterfly.kakaoPay.domain.repository.OrderDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminOrderDetailsService {

    private final OrderDetailsRepository orderDetailsRepository;

    public Page<OrderDetails> getAllOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return orderDetailsRepository.findAll(pageable);
    }

    @Transactional
    public void updateOrderStatus(Long orderId, OrderStatus newStatus) {
        OrderDetails order = orderDetailsRepository.findById(orderId)
                .orElseThrow(() -> new OrderException(ErrorCode.ORDER_NOT_FOUND, ErrorCode.ORDER_NOT_FOUND.getMessage()));

        order.setOrderStatus(newStatus);
        orderDetailsRepository.save(order);
    }
}
