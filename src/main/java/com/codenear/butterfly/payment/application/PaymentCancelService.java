package com.codenear.butterfly.payment.application;

import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.payment.domain.dto.request.CancelRequestDTO;
import com.codenear.butterfly.payment.domain.repository.SinglePaymentRepository;
import com.codenear.butterfly.payment.exception.PaymentException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentCancelService {
    private final List<PaymentCancel> cancelServices;
    private final SinglePaymentRepository singlePaymentRepository;

    /**
     * 결제 취소 분기처리
     *
     * @param request 결제 취소 DTO
     */
    public void cancelPayment(CancelRequestDTO request) {
        String provider = getProvider(request.getTid());
        PaymentCancel cancelService = cancelServices.stream()
                .filter(service -> service.getProvider().equals(provider))
                .findFirst()
                .orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND_PROVIDER, ErrorCode.PAYMENT_NOT_FOUND_PROVIDER.getMessage()));

        cancelService.cancel(request);
    }

    /**
     * provider를 가져온다 (TOSS, KAKAO, SinglePayment)
     *
     * @param tid
     * @return
     */
    private String getProvider(String tid) {
        return singlePaymentRepository.findProviderByTid(tid);
    }
}
