package com.codenear.butterfly.payment.application;

import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.payment.domain.dto.request.CancelRequestDTO;
import com.codenear.butterfly.payment.exception.PaymentException;
import com.codenear.butterfly.payment.kakaoPay.domain.repository.SinglePaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentCancelService {
    private final List<PaymentCancel> cancelServices;
    private final SinglePaymentRepository singlePaymentRepository;

    public void cancelPayment(CancelRequestDTO request) {
        String provider = getProvider(request.getTid());
        PaymentCancel cancelService = cancelServices.stream()
                .filter(service -> service.getProvider().equals(provider))
                .findFirst()
                .orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND_PROVIDER, ErrorCode.PAYMENT_NOT_FOUND_PROVIDER.getMessage()));

        cancelService.cancel(request);
    }

    private String getProvider(String tid) {
        return singlePaymentRepository.findProviderByTid(tid);
    }
}
