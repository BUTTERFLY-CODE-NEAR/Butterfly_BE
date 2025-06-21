package com.codenear.butterfly.payment.tossPay.application;

import com.codenear.butterfly.payment.domain.dto.request.BasePaymentRequestDTO;
import com.codenear.butterfly.payment.tossPay.domain.dto.ReadyResponseDTO;
import com.codenear.butterfly.payment.tossPay.domain.dto.TossPaymentCancelRequestDTO;
import org.springframework.stereotype.Service;

@Service
public interface TossPaymentService {
    ReadyResponseDTO paymentReady(BasePaymentRequestDTO basePaymentRequestDTO, Long memberId, String orderType);

    void confirm(Long memberId, String paymentKey, String orderId, int amount);

    void cancelPayment(TossPaymentCancelRequestDTO cancelRequestDTO);

    void failPayment(Long memberId, String productName, int quantity);
}
