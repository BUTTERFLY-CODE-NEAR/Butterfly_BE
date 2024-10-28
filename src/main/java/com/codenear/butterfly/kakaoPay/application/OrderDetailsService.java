package com.codenear.butterfly.kakaoPay.application;

import com.codenear.butterfly.kakaoPay.domain.OrderDetails;
import com.codenear.butterfly.kakaoPay.domain.dto.order.OrderDetailsDTO;
import com.codenear.butterfly.kakaoPay.domain.repository.OrderDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderDetailsService {

    private final OrderDetailsRepository orderDetailsRepository;

    public List<OrderDetailsDTO> getAllOrderDetails(Long memberId) {
        List<OrderDetails> orderDetailsList = orderDetailsRepository.findAllByMemberId(memberId);


        return orderDetailsList.stream()
                .map(this::convertToOrderDetailsDTO)
                .toList();
    }

    private OrderDetailsDTO convertToOrderDetailsDTO(OrderDetails orderDetails) {
        return new OrderDetailsDTO(
                orderDetails.getProductName(),
                orderDetails.getOptionName(),
                orderDetails.getProductImage(),
                orderDetails.getTotal(),
                orderDetails.getQuantity(),
                orderDetails.getOrderStatus()
        );
    }
}