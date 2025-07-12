package com.codenear.butterfly.payment.tossPay.application;

import com.codenear.butterfly.payment.domain.dto.request.BasePaymentRequestDTO;
import com.codenear.butterfly.payment.tossPay.domain.dto.ReadyResponseDTO;
import org.springframework.stereotype.Service;

@Service
public interface TossPaymentService {
    ReadyResponseDTO paymentReady(BasePaymentRequestDTO basePaymentRequestDTO, Long memberId, String orderType);

    void confirm(Long memberId, String paymentKey, String orderId, int amount);

    void failPayment(Long memberId, String productName, int quantity);
}
