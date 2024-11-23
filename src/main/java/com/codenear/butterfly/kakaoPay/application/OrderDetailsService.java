package com.codenear.butterfly.kakaoPay.application;

import com.codenear.butterfly.kakaoPay.domain.OrderDetails;
import com.codenear.butterfly.kakaoPay.domain.dto.order.OrderDetailsDTO;
import com.codenear.butterfly.kakaoPay.domain.repository.OrderDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderDetailsService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final OrderDetailsRepository orderDetailsRepository;

    public List<OrderDetailsDTO> getAllOrderDetails(Long memberId) {
        List<OrderDetails> orderDetailsList = orderDetailsRepository.findAllByMemberId(memberId);

        return orderDetailsList.stream()
                .sorted(Comparator.comparing(OrderDetails::getCreatedAt).reversed())
                .map(this::convertToOrderDetailsDTO)
                .toList();
    }

    private OrderDetailsDTO convertToOrderDetailsDTO(OrderDetails orderDetails) {
        return new OrderDetailsDTO(
                orderDetails.getOrderCode(),
                orderDetails.getCreatedAt().format(DATE_FORMATTER),
                orderDetails.getProductName(),
                orderDetails.getOptionName(),
                orderDetails.getProductImage(),
                orderDetails.getTotal(),
                orderDetails.getQuantity(),
                orderDetails.getOrderStatus()
        );
    }
}
