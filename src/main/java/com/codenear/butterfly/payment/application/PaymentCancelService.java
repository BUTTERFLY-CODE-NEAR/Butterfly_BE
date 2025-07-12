package com.codenear.butterfly.payment.application;

import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.payment.domain.OrderDetails;
import com.codenear.butterfly.payment.domain.dto.handler.CancelFreePaymentHandler;
import com.codenear.butterfly.payment.domain.dto.request.CancelRequestDTO;
import com.codenear.butterfly.payment.domain.repository.OrderDetailsRepository;
import com.codenear.butterfly.payment.domain.repository.SinglePaymentRepository;
import com.codenear.butterfly.payment.exception.PaymentException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentCancelService {
    private static final String FREE_PAYMENT = "CODENEAR_";
    private final List<PaymentCancel> cancelServices;
    private final PaymentService paymentService;
    private final SinglePaymentRepository singlePaymentRepository;
    private final OrderDetailsRepository orderDetailsRepository;

    /**
     * 결제 취소 분기처리
     *
     * @param request 결제 취소 DTO
     */
    public void cancelPayment(CancelRequestDTO request) {
        OrderDetails orderDetails = getOrderDetails(request.getOrderCode());

        if (request.getTid().startsWith(FREE_PAYMENT)) {
            paymentService.processPaymentCancel(new CancelFreePaymentHandler(orderDetails), orderDetails.getMember().getId());
            return;
        }

        String provider = getProvider(request.getTid());
        PaymentCancel cancelService = cancelServices.stream()
                .filter(service -> service.getProvider().equals(provider))
                .findFirst()
                .orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND_PROVIDER, ErrorCode.PAYMENT_NOT_FOUND_PROVIDER.getMessage()));

        cancelService.cancel(request, orderDetails);
    }

    /**
     * provider를 가져온다 (TOSS, KAKAO, SinglePayment)
     *
     * @param tid
     * @return provider
     */
    private String getProvider(String tid) {
        return singlePaymentRepository.findProviderByTid(tid);
    }

    /**
     * OrderDetails를 가져온다
     *
     * @param orderCode 주문 코드
     * @return orderDetails 객체
     */
    private OrderDetails getOrderDetails(String orderCode) {
        return orderDetailsRepository.findByOrderCode(orderCode);
    }
}
