package com.codenear.butterfly.payment.kakaoPay.application;

import com.codenear.butterfly.payment.domain.dto.request.BasePaymentRequestDTO;
import com.codenear.butterfly.payment.kakaoPay.domain.dto.ReadyResponseDTO;
import org.springframework.stereotype.Service;

@Service
public interface KakaoPaymentService {
    ReadyResponseDTO paymentReady(BasePaymentRequestDTO paymentRequestDTO, Long memberId, String orderType);

    void paymentApprove(String pgToken, Long memberId);

    void cancelPayment(Long memberId, String productName, int quantity);

    void failPayment(Long memberId, String productName, int quantity);

}
