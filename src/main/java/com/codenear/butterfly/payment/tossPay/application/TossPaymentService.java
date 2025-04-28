package com.codenear.butterfly.payment.tossPay.application;

import com.codenear.butterfly.payment.domain.dto.request.BasePaymentRequestDTO;
import com.codenear.butterfly.payment.domain.dto.request.CancelRequestDTO;
import org.springframework.stereotype.Service;

@Service
public interface TossPaymentService {
    void paymentReady(BasePaymentRequestDTO basePaymentRequestDTO, Long memberId, String orderType);

    void confirm(Long memberId, String paymentKey, String orderId, int amount);

    void cancelPayment(CancelRequestDTO cancelRequestDTO);

    void failPayment(Long memberId, String productName, int quantity);
}
