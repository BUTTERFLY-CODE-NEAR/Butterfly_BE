package com.codenear.butterfly.payment.domain.dto.handler;

import com.codenear.butterfly.payment.domain.Amount;
import com.codenear.butterfly.payment.domain.SinglePayment;
import com.codenear.butterfly.payment.domain.dto.request.BasePaymentRequestDTO;
import lombok.Getter;

@Getter
public class ApproveFreePaymentHandler extends ApproveHandler {
    private final BasePaymentRequestDTO basePaymentRequestDTO;

    public ApproveFreePaymentHandler(BasePaymentRequestDTO basePaymentRequestDTO, String orderId) {
        super(
                orderId,
                basePaymentRequestDTO.getProductName(),
                basePaymentRequestDTO.getQuantity(),
                basePaymentRequestDTO.getPoint()
        );
        this.basePaymentRequestDTO = basePaymentRequestDTO;
    }

    @Override
    public SinglePayment createSinglePayment(Long memberId) {
        return SinglePayment.freeOrderBuilder()
                .orderId(orderId)
                .memberId(memberId)
                .basePaymentRequestDTO(basePaymentRequestDTO)
                .buildFreeOrder();
    }

    @Override
    public Amount createAmount() {
        return Amount.freeOrderBuilder()
                .basePaymentRequestDTO(basePaymentRequestDTO)
                .buildFreeOrder();
    }

    @Override
    public Object getOrderDetailDto() {
        return basePaymentRequestDTO;
    }
}
